package com.riemann.rabbitmq.order.controller;

import com.riemann.rabbitmq.order.pojo.Order;
import com.riemann.rabbitmq.order.service.OrderService;
import com.riemann.rabbitmq.order.util.SnowFlakeWorkerUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SnowFlakeWorkerUtil snowFlakeWorkerUtil;

    @Value("${order.message.ttl}")
    private String orderMessageTtl;

    @RequestMapping("/")
    public String orderPage() {
        return "index";
    }

    @RequestMapping("/buy")
    public String buyGoods(@RequestParam("name") String name, @RequestParam("count") int count, Model model) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Order order = new Order();
        order.setId(snowFlakeWorkerUtil.nextId());
        order.setName(name);
        order.setCount(count);
        order.setStatus("未付款");
        order.setCancelReason("");
        order.setOrderTime(new Date());
        order.setOrderTimeStr(simpleDateFormat.format(new Date()));
        orderService.saveOrder(order);
        int time = Integer.parseInt(orderMessageTtl) / 1000;
        rabbitTemplate.convertAndSend("ex.order", "order", order);
        model.addAttribute("orderId", order.getId());
        model.addAttribute("time", time);
        return "pay";
    }

    @RequestMapping("/pay")
    public String payOrder(@RequestParam("id") Long id, Model model) {
        orderService.updateOrderStatusById(id, "已付款", "");
        List<Order> orders = orderService.getOrders();
        model.addAttribute("orders", orders);
        return "orders";
    }

    @RequestMapping("/orderlist")
    public String orders(Model model) {
        List<Order> orders = orderService.getOrders();
        model.addAttribute("orders", orders);
        return "orders";
    }
}
