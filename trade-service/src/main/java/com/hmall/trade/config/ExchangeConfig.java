package com.hmall.trade.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangeConfig {
//    @Bean
//    public DirectExchange directExchange() {  // 声明一个直连交换机
//        return new DirectExchange("pay.direct");  // 交换机名称
//    }
//    @Bean
//    public Queue queue() {  // 声明一个队列
////        return new Queue("trade.pay.success.queue");
//        return QueueBuilder  // 声明一个队列
//                .durable("trade.pay.success.queue.trade")  // 队列名称
//                .deadLetterExchange("dl.direct")  // 死信交换机
//                .build();  // 构建队列
//    }
//    @Bean
//    public Binding binding(DirectExchange directExchange, Queue queue) {  // 绑定队列到交换机
//        return BindingBuilder.bind(queue).to(directExchange).with("pay.success");  // 绑定路由键
//    }
}
