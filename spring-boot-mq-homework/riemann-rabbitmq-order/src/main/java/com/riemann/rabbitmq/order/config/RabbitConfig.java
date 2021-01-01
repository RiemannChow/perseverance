package com.riemann.rabbitmq.order.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {
    @Value("${order.message.ttl}")
    private String orderMessageTtl;

    @Bean
    public Queue queue() {
        Map<String, Object> props = new HashMap<>();
        // 消息生存时间
        props.put("x-message-ttl", Long.parseLong(orderMessageTtl));
        // 设置该队列所关联的死信交换器
        props.put("x-dead-letter-exchange", "ex.order.dlx");
        // 设置该队列所关联的死信交换器的routingkey
        props.put("x-dead-letter-routing-key", "order.dlx");

        return new Queue("queue.order", true, false, false, props);
    }

    @Bean
    public Queue queueDlx() {
        return new Queue("queue.order.dlx", true, false, false);
    }

    @Bean
    public Exchange exchange() {
        return new DirectExchange("ex.order", true, false, null);
    }

    // 死信交换器
    @Bean
    public Exchange exchangeDlx() {
        return new DirectExchange("ex.order.dlx", true, false, null);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with("order").noargs();
    }

    // 死信交换器绑定死信队列
    @Bean
    public Binding bindingDlx() {
        return BindingBuilder.bind(queueDlx()).to(exchangeDlx()).with("order.dlx").noargs();
    }
}
