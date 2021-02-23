package com.riemann.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    public Queue queue() {
        return new Queue("queue.biz.publisher.message.confirm", false, false, false, null);
    }

    @Bean
    public Exchange exchange() {
        return new DirectExchange("ex.biz.publisher.message.confirm", false, false, null);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with("biz.publisher.message.confirm").noargs();
    }
}
