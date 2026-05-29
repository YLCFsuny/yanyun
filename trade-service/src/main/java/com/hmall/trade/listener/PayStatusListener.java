package com.hmall.trade.listener;

import com.hmall.trade.domain.po.Order;
import com.hmall.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayStatusListener {

    private final IOrderService orderService;
    private final StringRedisTemplate redisTemplate;

    /**
     * 监听支付单状态变更消息
     * @param orderId 订单id
     */
    @RabbitListener(queues = "trade.pay.success.queue")
    public void handlePaySuccess(Long orderId, Message message, Channel channel) throws IOException {

        String messageId = message.getMessageProperties().getMessageId();
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        // 从Redis获取已重试次数
        String retryKey = "msg:retry:" + messageId;
        String countStr = redisTemplate.opsForValue().get(retryKey);
        int retryCount = 0;
        if (countStr != null) {
            try {
                retryCount = Integer.parseInt(countStr);
            } catch (NumberFormatException e) {
                log.warn("重试次数解析失败: {}", countStr);
            }
        }

        try {
            // 幂等性检查：检查消息是否已处理
            if (isMessageProcessed(messageId)) {
                log.info("消息已处理，跳过重复消费 - MessageId: {}, OrderId: {}", messageId, orderId);
                channel.basicAck(deliveryTag, false);
                return;
            }
            // 处理业务逻辑
            orderService.markOrderPaySuccess(orderId);
            // 标记消息已处理
            markMessageProcessed(messageId);

            channel.basicAck(deliveryTag, false);

            // 清除重试记录
            redisTemplate.delete(retryKey);
            log.info("支付状态消息处理成功 - OrderId: {}", orderId);
        } catch (Exception e) {
            log.error("支付状态消息处理失败 - OrderId: {}, Error: {}", orderId, e.getMessage(), e);
            log.error("处理失败，当前重试次数: {}", retryCount, e);
            if (retryCount < 2) {
                redisTemplate.opsForValue().set(retryKey, String.valueOf(retryCount + 1), 10L, TimeUnit.MINUTES);
                channel.basicNack(deliveryTag, false, true);
            } else {
                channel.basicNack(deliveryTag, false, false);
                redisTemplate.delete(retryKey);
            }
        }
    }

    @RabbitListener(queues = "trade.pay.dlx.queue")
    public void onPayStatusChange(Long orderId) {
        Order order = orderService.getById(orderId);
        if (order == null || order.getStatus() != 1) {
            log.error("订单不存在或状态异常：{}", orderId);
            return;
        }
        orderService.markOrderPaySuccess(orderId);
    }

    private boolean isMessageProcessed(String messageId) {
        String key = "msg:processed:" + messageId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    private void markMessageProcessed(String messageId) {
        String key = "msg:processed:" + messageId;
        redisTemplate.opsForValue().set(key, "1", 24L, TimeUnit.HOURS);
    }
}
