package com.hmall.pay.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class PayMessageSender implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

    private static final Logger log = LoggerFactory.getLogger(PayMessageSender.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        // 这两行是关键，让 RabbitTemplate 知道谁来处理回调和退回
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnsCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        // 这里是异步回调，消息到达/未到达Broker时会触发
        if (!ack) {
            log.error("【严重】消息发送到Broker失败，需要人工干预或重发，ID: {}", correlationData);
        }
    }

    @Override
    public void returnedMessage(ReturnedMessage returned) {
        // 消息到达了Broker，但是路由不到队列（比如队列被误删了）
        log.error("【严重】消息路由失败，已被Broker丢弃！消息: {}", returned.getMessage());
    }
}