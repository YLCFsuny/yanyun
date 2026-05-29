package com.hmall.api.client;

import com.hmall.api.client.fallback.ItemClientFallbackFactory;
import com.hmall.api.config.DefaultFeignConfig;
import com.hmall.api.dto.ItemDTO;
import com.hmall.api.dto.OrderDetailDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

//动态代理，调用服务端接口
@FeignClient(
        value = "item-service",
        configuration = DefaultFeignConfig.class,
        fallbackFactory = ItemClientFallbackFactory.class)
public interface ItemClient {
    @ApiOperation("根据id查询商品")
    @GetMapping("/items")
    List<ItemDTO> queryItemByIds(@RequestParam("ids") Collection<Long> ids);

    @ApiOperation("批量扣减库存")
    @PutMapping("/items/stock/deduct")
    void deductStock(@RequestBody List<OrderDetailDTO> items);

    @ApiOperation("批量恢复库存")
    @PutMapping("/items/stock/reStock")
    void reStock(Long orderId);
}
