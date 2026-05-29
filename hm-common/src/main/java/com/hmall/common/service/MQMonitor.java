package com.hmall.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;


//MQ监控服务
@Slf4j
@Component
public class MQMonitor {

    public void logMessageSend(String exchange, String routingKey, Object message) {
        log.info("MQ消息发送 - Exchange: {}, RoutingKey: {}, Message: {}",
                exchange, routingKey, message);
    }

    public void logMessageReceive(String queue, Message message) {
        log.info("MQ消息接收 - Queue: {}, MessageId: {}",
                queue, message.getMessageProperties().getMessageId());
    }

    public void logMessageProcessSuccess(String operation, Object result) {
        log.info("MQ消息处理成功 - Operation: {}, Result: {}", operation, result);
    }

    public void logMessageProcessError(String operation, Exception error) {
        log.error("MQ消息处理失败 - Operation: {}, Error: {}", operation, error.getMessage(), error);
    }
}