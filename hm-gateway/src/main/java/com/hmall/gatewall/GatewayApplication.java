package com.hmall.gatewall;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@ComponentScan("com.hmall")  // 确保扫描整个hmall包
public class GatewayApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(GatewayApplication.class, args);
        System.out.println("GatewayApplication 启动成功！");
    }

}
