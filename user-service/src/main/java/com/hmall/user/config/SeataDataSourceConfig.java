package com.hmall.user.config;

import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(name = "seata.enabled", havingValue = "true", matchIfMissing = true)
public class SeataDataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource(DataSource dataSource) {
        return new DataSourceProxy(dataSource);
    }
}
