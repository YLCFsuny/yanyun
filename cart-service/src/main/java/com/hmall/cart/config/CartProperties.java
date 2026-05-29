package com.hmall.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component  // 让Spring容器能够管理这个Bean
@ConfigurationProperties(prefix = "hm.cart")  // ConfigurationProperties注解：指定配置文件中的前缀
public class CartProperties {
    private  Integer maxItems;  // 最大商品数量
}
