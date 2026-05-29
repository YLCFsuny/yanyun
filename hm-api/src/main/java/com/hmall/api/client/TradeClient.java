package com.hmall.api.client;

import com.hmall.api.client.fallback.TradeClientFallbackFactory;
import com.hmall.api.config.DefaultFeignConfig;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(
        value= "trade-service",
        configuration = DefaultFeignConfig.class,
        fallbackFactory = TradeClientFallbackFactory.class)
public interface TradeClient {
    @ApiOperation("标记订单已支付")
    @ApiImplicitParam(name = "orderId", value = "订单id", paramType = "path")
    @PutMapping("/orders/{orderId}")
    void markOrderPaySuccess(@PathVariable("orderId") Long orderId);
}
