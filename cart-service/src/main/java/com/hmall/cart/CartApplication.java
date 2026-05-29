package com.hmall.cart;

import com.hmall.api.config.DefaultFeignConfig;
import com.hmall.cart.config.LoadBalancerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@Slf4j  // 日志
@LoadBalancerClients(defaultConfiguration = LoadBalancerConfiguration.class)  // 开启负载均衡功能
@EnableFeignClients(basePackages = "com.hmall.api.client", defaultConfiguration = DefaultFeignConfig.class)  // 开启Feign客户端功能
@MapperScan("com.hmall.cart.mapper")  // 扫描Mapper接口
@SpringBootApplication  // 启动类
public class CartApplication {
    public static void main(String[] args) {

        SpringApplication.run(CartApplication.class, args);
        System.out.println("项目启动成功");

    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}