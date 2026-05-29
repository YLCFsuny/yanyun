package com.hmall.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({MybatisPlusInterceptor.class, BaseMapper.class})  // 必须存在这两个类才会生效
public class MyBatisConfig {
    @Bean  // 注册MybatisPlusInterceptor拦截器，用于分页查询等功能
    @ConditionalOnMissingBean  // 如果容器中已经存在MybatisPlusInterceptor实例，则不创建新的实例
    // 这个注解的作用是，如果容器中已经存在一个MybatisPlusInterceptor实例，那么这个Bean就不会被创建。
    // 这样可以避免重复创建MybatisPlusInterceptor实例，从而提高应用的性能和效率。
    // 这个注解通常用于配置类中，用于注册Bean。
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatis_plus_interceptor = new MybatisPlusInterceptor();
        // 1.分页拦截器
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);  // 指定数据库类型为MySQL
        paginationInnerInterceptor.setMaxLimit(1000L);  // 设置最大查询条数，防止恶意查询
        mybatis_plus_interceptor.addInnerInterceptor(paginationInnerInterceptor);  // 添加分页拦截器
        return mybatis_plus_interceptor;
    }
}