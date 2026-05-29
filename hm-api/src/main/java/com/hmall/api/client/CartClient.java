package com.hmall.api.client;

import com.hmall.api.client.fallback.CartClientFallbackFactory;
import com.hmall.api.config.DefaultFeignConfig;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

@FeignClient(
        value = "cart-service",
        configuration = DefaultFeignConfig.class,
        fallbackFactory = CartClientFallbackFactory.class)
public interface CartClient {

    @ApiOperation("批量删除购物车中商品")
    @ApiImplicitParam(name = "ids", value = "购物车条目id集合")
    @DeleteMapping("/carts")
    void deleteCartItemByIds(@RequestParam("ids") Collection<Long> ids);

}
