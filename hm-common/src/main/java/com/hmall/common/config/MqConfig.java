package com.hmall.common.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnClass(RabbitTemplate.class)
public class MqConfig {

    @Bean
    // 配置消息转换器，将消息序列化为JSON格式
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }


    // 延迟交换机，
    // 延迟插件方式；插件：rabbitmq_delayed_message_exchanges-3.10.1.ez
    @Bean
    public CustomExchange delayedExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange("trade.delay.direct",
                "x-delayed-message",
                true, false, args);
    }
    // 延迟队列
    @Bean
    public Queue delayedOrderQueue() {
        return QueueBuilder.durable("trade.delay.order.queue").build();
    }
    // 绑定关系-延迟
    @Bean
    public Binding delayedOrderBinding() {
        return BindingBuilder.bind(delayedOrderQueue())
                .to(delayedExchange())
                .with("trade.order.query")  // 路由键需要与MQConstants.DELAY_ORDER_KEY一致
                .noargs();
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 交易服务交换机
    @Bean
    public DirectExchange tradeExchange() {
        return new DirectExchange("trade.order.direct");
    }
    // 交易服务队列
    @Bean
    public Queue tradeQueue(){
        return QueueBuilder.durable("trade.order.queue")
                .deadLetterExchange("dle.direct")      // 死信交换机
                .deadLetterRoutingKey("order.dle")    // 死信路由键
                .ttl(1800000)                              // ⭐ 新增：设置消息30分钟过期（订单30分钟未支付，则失效）
                .maxLength(1000)                     // ⭐ 可选：设置队列最大长度
                .build();
    }
    // 绑定关系-交易服务
    @Bean
    public Binding tradeBinding() {
        return BindingBuilder.bind(tradeQueue())
                .to(tradeExchange())
                .with("trade.order");
    }
    // ========== 死信组件 ==========
    // 死信交换机
    @Bean
    public DirectExchange orderDeadLetterExchange() {
        return new DirectExchange("dle.direct");
    }
    // 死信队列
    @Bean
    public Queue orderDeadLetterQueue() {
        return QueueBuilder.durable("order.dle.queue").build();
    }
    // 绑定关系-死信
    @Bean
    public Binding orderDeadLetterBinding() {
        return BindingBuilder.bind(orderDeadLetterQueue())
              .to(orderDeadLetterExchange())
              .with("order.dle");
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 支付成功-交换机
    @Bean
    public DirectExchange paySuccessExchange() {
        return new DirectExchange("pay.direct");
    }
    // 支付成功队列（带死信）
    @Bean
    public Queue paySuccessQueue() {
        return QueueBuilder.durable("trade.pay.success.queue")
                .deadLetterExchange("dlx.direct")  // 死信交换机
                .deadLetterRoutingKey("pay.dlx")  // 死信路由键
                .ttl(1800000)                          // ⭐ 新增：设置消息30分钟过期（订单30分钟未支付，则失效）
                .maxLength(1000)                 // ⭐ 可选：设置队列最大长度
                .build();
    }
    // 绑定关系-支付成功
    @Bean
    public Binding paySuccessBinding() {
        return BindingBuilder.bind(paySuccessQueue())
                .to(paySuccessExchange())
                .with("pay.success");
    }
    // ========== 死信组件 ==========
    // 死信交换机
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("dlx.direct");
    }
    // 死信队列
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("trade.pay.dlx.queue").build();
    }
    // 绑定关系-死信
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with("pay.dlx");
    }
}
