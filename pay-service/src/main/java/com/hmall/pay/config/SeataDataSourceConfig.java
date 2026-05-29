package com.hmall.pay.config;

import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(name = "seata.enabled", havingValue = "true", matchIfMissing = true)
public class SeataDataSourceConfig {

    /**
     * 将原始 Druid 数据源包装为 Seata 的代理数据源
     * @param dataSource 由 Spring Boot 自动配置的原始数据源
     * @return 代理后的数据源
     */
    @Bean
    @Primary // 【关键】必须声明为主数据源，避免与自动配置的 DataSource 冲突
    public DataSource dataSource(DataSource dataSource) { // 参数类型改为 DataSource
        // 这一步是关键：将原有的 DruidDataSource 交给 Seata 代理
        return new DataSourceProxy(dataSource);
    }
}

