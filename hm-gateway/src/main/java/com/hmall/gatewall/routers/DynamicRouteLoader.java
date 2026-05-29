package com.hmall.gatewall.routers;

import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import static org.reflections.Reflections.log;

@RequiredArgsConstructor  // 生成构造器 注入 NacosConfigManager 和 RouteDefinitionWriter   用于动态路由的加载和更新
@Component
@Slf4j
// 动态路由加载器
// 1. 从Nacos中读取配置文件，解析为RouteDefinition
// 2. 更新路由表
// 3. 监听配置文件的变更，更新路由表
// 4. 路由表的更新，需要使用RouteDefinitionWriter来更新
public class DynamicRouteLoader {

    private final NacosConfigManager nacosConfigManager;
    private final RouteDefinitionWriter routeDefinitionWriter;

    private final String dataId = "gateway-routes.json";
    private final String group = "DEFAULT_GROUP";
    private final Set<String> routeIds = new HashSet<>();  // 唯一，无序，不重复

    @PostConstruct // 项目启动时，初始化路由配置监听器（在Bean初始化后执行）
    public void initRouteConfigListener() throws NacosException {
        log.debug("初始化路由配置监听器");
        //项目启动时，先拉取一次配置，并且添加配置监听器
        String configInfo = nacosConfigManager.getConfigService()
                .getConfigAndSignListener(
                        dataId,  // 配置文件名称
                        group,   // 配置文件分组
                        5000,    // 配置文件的超时时间，单位：毫秒
                        new Listener() {
                    @Override
                    public Executor getExecutor() {  // 线程池
                        return null;
                    }  // 定义一个线程池TODO
                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        //监听到配置变更，需要去更新路由表
                        updateConfigInfo(configInfo);
                    }
                });
        //第一次读取到配置，也需要更新到路由表
        updateConfigInfo(configInfo);
    }

    //更新路由表
    public void updateConfigInfo (String configInfo){
        log.debug("更新路由表：{}", configInfo);
        // 1. 解析配置文件，转为RouteDefinition
        List<RouteDefinition> routeDefinitions = JSONUtil.toList(configInfo, RouteDefinition.class);
        // 删除旧的路由表
        for (String routeId : routeIds) {
            routeDefinitionWriter.delete(Mono.just(routeId)).subscribe();
        }
        routeIds.clear();
        // 2. 更新路由表
        for (RouteDefinition routeDefinition : routeDefinitions) {
            // 2.1 添加路由表
            routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
            // 2.1 记录路由id，用于下一次更新路由表时删除路由
            routeIds.add(routeDefinition.getId());
        }
    }
}
