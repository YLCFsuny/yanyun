package com.hmall.gatewall.filters;

import com.hmall.common.exception.UnauthorizedException;
import com.hmall.gatewall.config.AuthProperties;
import com.hmall.gatewall.config.JwtProperties;
import com.hmall.gatewall.utils.JwtTool;
import com.hmall.gatewall.utils.TokenRefreshUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

@Slf4j
@Component  // 让Spring容器管理这个Bean
@RequiredArgsConstructor  // 生成构造器   注入 JwtTool 和 AuthProperties
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtTool jwtTool;
    private final AuthProperties authProperties;
    private final JwtProperties jwtProperties;
    private final TokenRefreshUtil tokenRefreshUtil; // 改为依赖注入
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
//    private final TokenRefreshUtil tokenRefreshUtil = new TokenRefreshUtil();


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 模拟登录校验逻辑
        // 获取request对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 判断是否需要登录校验
        if (isExclude(request.getPath().toString())) {  // 如果是排除的路径，则不进行登录校验
            // 放行
            return chain.filter(exchange);
        }
        // 获取请求头中的token
        // String token = request.getHeaders().getFirst("Authorization");  // 这种方式只能获取到第一个值
        String token = null;
        List<String> authorizationList = request.getHeaders().get("Authorization");  // 这种方式可以获取到所有的值
        if (authorizationList != null && !authorizationList.isEmpty()) {
            token = authorizationList.get(0);  // 取第一个值
        }
        // 校验并解析token
        Long userId = null;
        try {
            userId = jwtTool.parseToken(token);
            // 添加token刷新逻辑
            Date expiration = jwtTool.getExpiration(token);
            if (tokenRefreshUtil.shouldRefresh(expiration)) {
                log.info("检测到token即将过期，自动刷新token，用户ID: {}", userId);
                String newToken = tokenRefreshUtil.refreshToken(token, jwtTool, jwtProperties.getTokenTTL());

                // 在响应头中添加新的token
                response.getHeaders().set("X-Refresh-Token", newToken);
                response.getHeaders().set("Access-Control-Expose-Headers", "X-Refresh-Token");

                log.info("token刷新成功，新token已添加到响应头");
            }
        } catch (UnauthorizedException e) {
//            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // 传递用户信息
        String userInfo = userId.toString();
        ServerWebExchange swe = exchange.mutate()  //
                .request(builder -> builder.header("user-info", userInfo))
                .build();
        // 放行
        return chain.filter(swe);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private boolean isExclude(String path) {
        // return authProperties.getExclude().stream().anyMatch(path::startsWith);
        for (String pathPatten : authProperties.getExcludePaths()) {
            if (antPathMatcher.match(pathPatten, path)) {
                return true;
            }
        }
        return false;
    }
}
