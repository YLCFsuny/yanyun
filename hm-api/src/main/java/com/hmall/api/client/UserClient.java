package com.hmall.api.client;

import com.hmall.api.client.fallback.UserClientFallbackFactory;
import com.hmall.api.config.DefaultFeignConfig;
import com.hmall.api.dto.DeductDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        value = "user-service",
        configuration = DefaultFeignConfig.class,
        fallbackFactory = UserClientFallbackFactory.class)
public interface UserClient {
    @ApiOperation("扣减余额")
    @PutMapping("/users/money/deduct")
    void deductMoney(@RequestBody DeductDTO deductDTO);
}
