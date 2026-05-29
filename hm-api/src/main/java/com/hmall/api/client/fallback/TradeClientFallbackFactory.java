package com.hmall.api.client.fallback;

import com.hmall.api.client.TradeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component // 让Spring容器能够管理这个Bean
public class TradeClientFallbackFactory extends BaseFallbackFactory<TradeClient> {
    @Override
    public TradeClient create(Throwable cause) {
        log.error("交易服务调用失败", cause);
        return new TradeClient() {
            @Override
            public void markOrderPaySuccess(Long orderId) {
                throw createFallbackException(cause, "trade-service");
            }
        };
    }
}
