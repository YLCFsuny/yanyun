package com.hmall.api.client.fallback;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public abstract class BaseFallbackFactory<T> implements FallbackFactory<T> {
    protected ResponseStatusException createFallbackException(Throwable cause, String serviceName) {
        if (cause instanceof FeignException.ServiceUnavailable) {
            return new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    String.format("%s服务暂不可用", serviceName)
            );
        } else if (cause instanceof FeignException.BadRequest) {
            return new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("%s服务请求参数错误", serviceName)
            );
        } else {
            return new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("%s服务调用失败", serviceName)
            );
        }
    }
}