package com.hmall.api.client.fallback;

import com.hmall.api.client.PayClient;
import com.hmall.api.dto.PayOrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class PayClientFallbackFactory implements FallbackFactory<PayClient> {
    @Override
    public PayClient create(Throwable cause) {
        log.warn("支付服务调用发生故障，已降级处理。异常原因: {}", cause.getMessage());

        return new PayClient() {
            @Override
            public PayOrderDTO queryPayOrderByBizOrderNo(Long id) {
                // 1. 记录日志，说明具体哪个业务的订单查询失败
                log.error("根据业务订单号查询支付订单失败。业务订单ID: {}, 降级原因: {}", id, cause.getMessage());
                //直接抛出可控的异常，让上游控制器统一处理（更推荐）
                // 这样上游服务可以通过异常处理器返回统一的错误格式
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "支付服务暂不可用，请稍后重试。原始错误: " + cause.getMessage()
//                return null;
                );
            }
        };
    }
}