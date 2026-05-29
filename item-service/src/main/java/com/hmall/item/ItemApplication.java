package com.hmall.item;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.hmall.item.mapper")
@SpringBootApplication
@Slf4j
public class ItemApplication {
    public static void main(String[] args) {

        SpringApplication.run(ItemApplication.class, args);
        System.out.println("项目启动成功");

    }
}