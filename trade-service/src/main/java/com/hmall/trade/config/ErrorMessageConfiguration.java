package com.hmall.trade.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息恢复器
 */
@Configuration  // 配置类注解
public class ErrorMessageConfiguration {
    @Bean
    // 定义消息恢复器
    public MessageRecoverer errorMessageRecoverer(RabbitTemplate rabbitTemplate  // RabbitTemplate 用于发送消息
                                                 ) {
        return new RepublishMessageRecoverer(rabbitTemplate,  // 消息恢复器
                                            "error.direct",   // 交换机
                                            "error");         // 路由键
        //return new RepublishMessageRecoverer(rabbitTemplate, "pay.direct", "pay.success");
    }
    @Bean
    // 定义交换机
    public DirectExchange errorMessageExchange() {
        return new DirectExchange("error.direct");  // 定义一个名为"error.direct"的交换机，类型为DirectExchange
    }
    @Bean
    // 定义队列
    public Queue errorMessageQueue() {
        return new Queue("error.queue");  // 定义一个名为"error.queue"的队列
    }
    @Bean
    // 绑定队列到交换机
    public Binding errorMessageBinding() {
        return BindingBuilder  // 绑定队列到交换机，路由键为"error"
                .bind(errorMessageQueue())  // 绑定的队列
                .to(errorMessageExchange())  // 绑定的交换机
                .with("error");  // 路由键
    }
}
