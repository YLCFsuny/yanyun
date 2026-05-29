package com.hmall.gatewall.filters;

import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
// 自定义过滤器工厂类，用于创建自定义过滤器实例
// 继承AbstractGatewayFilterFactory类，实现apply方法，用于创建自定义过滤器实例
public class PrintAnyGatewayFilterFactory extends AbstractGatewayFilterFactory<PrintAnyGatewayFilterFactory.Config> {
    @Override
    public GatewayFilter apply(Config config) {
        // 自定义过滤器逻辑
        return new OrderedGatewayFilter(new GatewayFilter() {    //匿名内部类(不能实现其他接口)
            @Override
            // 实现filter方法，用于处理请求和响应
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                // Add your custom logic here
                String token = exchange.getRequest().getHeaders().getFirst("Authorization");
                System.out.println("token:"+token);

                System.out.println("config:"+config);
                System.out.println("config.a:"+config.getA());
                System.out.println("config.b:"+config.getB());
                System.out.println("config.c:"+config.getC());

                return chain.filter(exchange);
            }
        },1);
    }

    //自定义配置属性，用于配置过滤器的参数
    @Data
    public static class Config {
        private String a;
        private String b;
        private String c;
    }

    // 配置属性的顺序，用于指定配置属性的顺序，默认按照配置属性的名称排序
    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("a","b","c");
    }

    //将配置类Config字节码传递给父类的构造函数，父类负责帮我们读取yaml配置，用于创建过滤器实例
    public PrintAnyGatewayFilterFactory() {
        super(Config.class);
    }



}
