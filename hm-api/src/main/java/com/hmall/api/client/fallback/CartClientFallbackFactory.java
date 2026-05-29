package com.hmall.api.client.fallback;

import com.hmall.api.client.CartClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@Component
public class CartClientFallbackFactory extends BaseFallbackFactory<CartClient> {
    @Override
    public CartClient create(Throwable cause) {
        log.error("购物车服务调用失败", cause);
        return new CartClient() {
            @Override
            public void deleteCartItemByIds(Collection<Long> ids) {
                throw createFallbackException(cause, "cart-service");
            }
        };
    }
}