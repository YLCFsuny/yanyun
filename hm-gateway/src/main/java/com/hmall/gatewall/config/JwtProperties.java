package com.hmall.gatewall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data // 自动生成getter和setter方法
@Component  // 表示这是一个Spring组件，会被Spring容器管理
@ConfigurationProperties(prefix = "hm.jwt")  // 配置属性前缀为"hm.jwt"
public class JwtProperties {
    private Resource location;  // 配置文件的位置
    private String password;
    private String alias;
    private Duration tokenTTL = Duration.ofMinutes(30);
}
