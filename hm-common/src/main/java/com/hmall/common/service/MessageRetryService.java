package com.hmall.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service  // 确保这个类被Spring加载为服务类
@RequiredArgsConstructor
public class MessageRetryService {

    private final RabbitTemplate rabbitTemplate;

    public void sendWithRetry(String exchange, String routingKey, Object message,
                              int maxRetries, long retryInterval) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                rabbitTemplate.convertAndSend(exchange, routingKey, message);
//                rabbitTemplate.convertAndSend(exchange, routingKey, message, m -> {
//                    m.getMessageProperties().setExpiration("15000");  // 设置TTL（消息存活时间）15秒
//                       或                    .setDelay(15000);        // 设置延迟（需要延迟插件）
//                    return m;
//                });
                log.info("消息发送成功 - Exchange: {}, RoutingKey: {}", exchange, routingKey);
                return;
            } catch (Exception e) {
                log.warn("消息发送失败，第{}次重试 - Exchange: {}, RoutingKey: {}", i+1, exchange, routingKey, e);
                if (i == maxRetries - 1) {
                    throw new RuntimeException("消息发送失败，已达到最大重试次数", e);
                }
                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("重试过程被中断", ie);
                }
            }
        }
    }
}