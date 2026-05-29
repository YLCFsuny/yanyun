package com.hmall.api.client.fallback;

import com.hmall.api.client.UserClient;
import com.hmall.api.dto.DeductDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class UserClientFallbackFactory extends BaseFallbackFactory<UserClient>{
    @Override
    public UserClient create(Throwable cause) {
        log.error("用户服务调用失败", cause);
        return new UserClient() {
            @Override
            public void deductMoney(DeductDTO deductDTO) {
                throw createFallbackException(cause, "user-service");
            }
        };
    }
}
