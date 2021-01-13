package com.riemann.rabbitmq.order.service;

import com.riemann.rabbitmq.order.pojo.Order;

import java.util.List;

public interface OrderService {
    int saveOrder(Order order);
    List<Order> getOrders();
    int updateOrderStatusById(long id, String status, String cancelReason);
    void setOrderCancelStatusById(long id);
}
