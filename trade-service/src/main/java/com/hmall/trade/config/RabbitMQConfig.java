package com.hmall.trade.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration  // 配置类注解   配置RabbitMQ监听器容器工厂
public class RabbitMQConfig {

    @Bean  // 配置RabbitMQ监听器容器工厂
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory  // 连接工厂
                                                                              ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();  // 创建监听器容器工厂
        factory.setConnectionFactory(connectionFactory);  // 设置连接工厂
        // 配置消费者失败后，消息重新入队
        factory.setDefaultRequeueRejected(true);  // 配置消费者失败后，消息重新入队
        return factory;
    }
}
