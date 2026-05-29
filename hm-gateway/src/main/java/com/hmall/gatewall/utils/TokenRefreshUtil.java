package com.hmall.gatewall.utils;

import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component  // 关键添加：让Spring容器能够管理这个Bean
public class TokenRefreshUtil {

    private static final Duration REFRESH_THRESHOLD = Duration.ofMinutes(5);

    public boolean shouldRefresh(Date expiration) {
        if (expiration == null) {
            return false;
        }
        return Duration.between(Instant.now(), expiration.toInstant())
                .compareTo(REFRESH_THRESHOLD) <= 0;
    }

    public String refreshToken(String oldToken, JwtTool jwtTool, Duration ttl) {
        // 解析旧token获取用户信息
        Long userId = jwtTool.parseToken(oldToken);
        return jwtTool.createToken(userId, ttl);
    }
}
