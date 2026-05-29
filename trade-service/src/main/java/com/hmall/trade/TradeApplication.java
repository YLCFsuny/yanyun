package com.hmall.trade;

import com.hmall.api.config.DefaultFeignConfig;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.hmall.api.client", defaultConfiguration = DefaultFeignConfig.class)  // 开启Feign客户端功能
@MapperScan("com.hmall.trade.mapper")
@SpringBootApplication
@Slf4j
public class TradeApplication {
    public static void main(String[] args) {

        SpringApplication.run(TradeApplication.class, args);
        System.out.println("项目启动成功");

    }
}