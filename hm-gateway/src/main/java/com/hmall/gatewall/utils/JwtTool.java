package com.hmall.gatewall.utils;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.hmall.common.exception.UnauthorizedException;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.Duration;
import java.util.Date;

@Component  // 关键添加：让Spring容器能够管理这个Bean
public class JwtTool {

    private final JWTSigner jwtSigner;

    public JwtTool(KeyPair keyPair) {
        // 1.创建jwt的签名器
        this.jwtSigner = JWTSignerUtil.createSigner("rs256", // 算法 RSASSA-PKCS1-v1_5
                                                    keyPair  // 秘钥对
        );
    }
    /**
     * 创建 access-token
     * @return access-token
     */
    public String createToken(Long userId, Duration ttl) {
        return JWT.create()  // 创建jwt
                .setPayload("user", userId)  // 设置用户信息
                .setExpiresAt(new Date(System.currentTimeMillis() + ttl.toMillis()))  // 设置过期时间
                .setSigner(jwtSigner)  // 设置签名器
                .sign();  // 生成token
    }

    /**
     * 解析token
     * @param token token
     * @return 用户id
     */
    public Long parseToken(String token) {
        if (token == null) {
            throw new UnauthorizedException("未登录");
        }

        JWT jwt = parseToJWT(token);
        return extractUserId(jwt);
    }
    /**
     * 获取token的过期时间
     */
    public Date getExpiration(String token) {
        JWT jwt = parseToJWT(token);
        // 使用getPayload方法获取过期时间
        Object expiresAt = jwt.getPayload("exp");
        if (expiresAt == null) {
            throw new UnauthorizedException("token没有设置过期时间");
        }
        // 将时间戳转换为Date对象
        long expirationTime = Long.parseLong(expiresAt.toString()) * 1000L;
        return new Date(expirationTime);
    }
    /**
     * 新增方法：解析token为JWT对象，用于获取过期时间等信息
     */
    public JWT parseToJWT(String token) {
        if (token == null) {
            throw new UnauthorizedException("未登录");
        }
        JWT jwt;
        try {
            jwt = JWT.of(token).setSigner(jwtSigner);
        } catch (Exception e) {
            throw new UnauthorizedException("无效的token", e);
        }
        if (!jwt.verify()) {
            throw new UnauthorizedException("无效的token");
        }
        try {
            JWTValidator.of(jwt).validateDate();
        } catch (ValidateException e) {
            throw new UnauthorizedException("token已经过期");
        }
        return jwt;
    }
    /**
     * 从JWT对象中提取用户ID
     */
    private Long extractUserId(JWT jwt) {
        Object userPayload = jwt.getPayload("user");
        if (userPayload == null) {
            throw new UnauthorizedException("无效的token");
        }
        try {
            return Long.valueOf(userPayload.toString());
        } catch (RuntimeException e) {
            throw new UnauthorizedException("无效的token");
        }
    }
}