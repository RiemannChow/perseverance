package com.riemann.rabbitmq.order.listener;

import com.riemann.rabbitmq.order.pojo.Order;
import com.riemann.rabbitmq.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReceviceMessageListener {
    @Autowired
    private OrderService orderService;

    @RabbitHandler
    @RabbitListener(queues = "queue.order.dlx")
    public void receviceMessage(Order order) {
        if (order.getStatus().equals("已付款")) {
            return;
        }
        log.info("死信队列接收到消息，{}过期未支付，订单已取消", order.id);
        log.info("订单详情：{}", order.toString());
        orderService.setOrderCancelStatusById(order.id);
    }
}
