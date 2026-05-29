package com.hmall.trade.listener;

import com.hmall.api.client.PayClient;
import com.hmall.api.dto.PayOrderDTO;
import com.hmall.trade.constants.MQConstants;
import com.hmall.trade.domain.po.Order;
import com.hmall.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDelayMessageListener {

    private final IOrderService orderService;
    private final PayClient payClient;

    @RabbitListener(queues = MQConstants.TRADE_DELAY_ORDER_QUEUE)
    public void listenerOrderDelayMessage(Long orderId){
        log.info("收到订单延迟消息：{}", orderId);
        // 1.查询订单状态
        Order order = orderService.getById(orderId);
        if(order == null || order.getStatus() != 1){
            log.info("订单不存在或已支付，OrderId: {}", orderId);
            return;
        }
        // 2.如果订单状态为未支付，查询支付流水状态
        PayOrderDTO payOrder = payClient.queryPayOrderByBizOrderNo(orderId);
        if(payOrder != null && payOrder.getStatus() == 3){
            //已支付，标记订单为已支付
            orderService.markOrderPaySuccess(orderId);
        }else{
            //未支付，取消订单，恢复库存
            orderService.cancelOrder(orderId);
        }
    }
}
