package com.hmall.api.config;

import com.hmall.api.client.fallback.ItemClientFallbackFactory;
import com.hmall.api.client.fallback.PayClientFallbackFactory;
import com.hmall.common.utils.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Retryer;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

// 配置Feign的日志级别和请求拦截器
public class DefaultFeignConfig {
    @Bean  // 配置Feign的日志级别
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public RequestInterceptor userInfoRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                // 获取当前登录用户的信息
                Long userId = UserContext.getUser();
                if (userId != null) {
                    // 将用户信息添加到请求头中
                    requestTemplate.header("user-info", userId.toString());
                }
                // 添加请求追踪ID
                requestTemplate.header("X-Request-ID", java.util.UUID.randomUUID().toString());
            }
        };
    }
    @Bean
    public Retryer feignRetryer() {
        // 重试机制：间隔100ms，最大间隔1s，重试3次
        return new Retryer.Default(100, TimeUnit.SECONDS.toMillis(1), 3);
    }
    @Bean
    public ItemClientFallbackFactory itemClientFallbackFactory() {
        return new ItemClientFallbackFactory();
    }
    @Bean
    public PayClientFallbackFactory payClientFallbackFactory() {
        return new PayClientFallbackFactory();
    }
}
