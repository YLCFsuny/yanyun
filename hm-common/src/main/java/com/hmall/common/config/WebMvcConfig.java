package com.hmall.common.config;

import com.hmall.common.interceptors.UserInfoInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration  // 注解告诉Spring容器："这个类包含了Bean的定义，应该被Spring管理"
@ConditionalOnWebApplication  // 仅在Web应用程序环境下生效；Gateway没有DispatcherServlet，所以Common拦截器不会在Gateway中生效
@ConditionalOnClass(DispatcherServlet.class)  // 仅在存在DispatcherServlet类时生效
@RequiredArgsConstructor  // Lombok注解，用于注入依赖，生成包含所有final字段的构造函数
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册用户信息拦截器，优先级最高
        registry.addInterceptor(new UserInfoInterceptor())
                .order(1)  // 设置执行顺序
                .addPathPatterns("/**")  // 拦截所有路径
                .excludePathPatterns(  // 排除路径
                        "/error",
                        "/favicon.ico",
                        "/v2/**",
                        "/v3/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/doc.html"
                );

        log.info("用户信息拦截器配置完成");
    }

//    private final JwtToolP jwtToolP;
//
//    // 需要手动控制Bean创建时，或者Bean需要参数化配置时
//    @Bean  // 注解告诉Spring容器："这个方法返回的对象应该被Spring管理，成为一个Bean"
//    public AuthenticationInterceptor authenticationInterceptor() {
//        return new AuthenticationInterceptor(jwtToolP, false); // 默认不强制认证
//    }
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(authenticationInterceptor())
//                .order(1)
//                .addPathPatterns("/**")
//                .excludePathPatterns(
//                        "/error",
//                        "/favicon.ico",
//                        "/v2/**",
//                        "/v3/**",
//                        "/swagger-resources/**",
//                        "/webjars/**",
//                        "/doc.html"
//                );
//    }

}