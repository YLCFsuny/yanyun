package com.hmall.gatewall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component  // 表示这是一个Spring组件，会被Spring容器管理
@ConfigurationProperties(prefix = "hm.auth")  // 读取配置文件中的前缀为hm.auth的属性
// 读取配置文件中的前缀为hm.auth的属性，将属性值注入到AuthProperties对象中
// 例如：hm.auth.includePaths = /api/v1/**,/api/v2/**
// 则AuthProperties对象中的includePaths属性值为：/api/v1/**,/api/v2/**
// 这样可以方便地管理和配置需要进行认证的路径
public class AuthProperties {
    private List<String> includePaths;  // 包含的路径
    private List<String> excludePaths;  // 排除的路径
}
