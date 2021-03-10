package com.riemann.rabbitmq.order.service.impl;

import com.riemann.rabbitmq.order.mapper.OrderMapper;
import com.riemann.rabbitmq.order.pojo.Order;
import com.riemann.rabbitmq.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public int saveOrder(Order order) {
        orderMapper.addOrder(order);
        return 0;
    }

    @Override
    public List<Order> getOrders() {
        return orderMapper.getOrders();
    }

    @Override
    public int updateOrderStatusById(long id, String status, String cancelReason) {
        return orderMapper.updateOrderStatusById(id, status, cancelReason);
    }

    @Override
    public void setOrderCancelStatusById(long id) {
        Order order = orderMapper.getOrderById(id);
        // 如果是已经付款的订单，就不再处理
        if (order.getStatus().equals("已付款")) {
            return;
        }
        // 未付款的订单，标记为已取消
        updateOrderStatusById(id, "已取消", "过期未支付");
    }
}
