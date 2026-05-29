package com.hmall.trade.constants;

public interface MQConstants {
    String TRADE_DELAY_DIRECT = "trade.delay.direct";
    String TRADE_DELAY_ORDER_QUEUE = "trade.delay.order.queue";
    String DELAY_ORDER_KEY = "trade.order.query";

    String TRADE_ORDER_DIRECT = "trade.order.direct";  // 普通交换机
    String TRADE_ORDER = "trade.order";
    String ORDER_DLE_QUEUE = "order.dle.queue";
    String ORDER_DLE = "order.dle";    // 死信路由键
}



