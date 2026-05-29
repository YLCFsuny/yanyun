package com.hmall.cart.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.loadbalancer.NacosLoadBalancer;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 负载均衡配置
 */
@Configuration  // 配置类
public class LoadBalancerConfiguration {
    @Bean
    public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(  // 负载均衡器
            Environment environment,  // 环境变量
            LoadBalancerClientFactory loadBalancerClientFactory,  // 负载均衡器工厂
            NacosDiscoveryProperties nacosDiscoveryProperties) {  // Nacos 发现属性
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);  // 获取负载均衡器名称
        return new NacosLoadBalancer(  // Nacos 负载均衡器
                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),  // 服务实例列表提供者
                name,  // 负载均衡器名称
                nacosDiscoveryProperties  // Nacos 发现属性
        );
    }
}
