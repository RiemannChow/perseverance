package com.riemann.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    public Queue queue() {
        return new Queue("q.biz", false, false, false, null);
    }

    @Bean
    public Exchange exchange() {
        return new DirectExchange("ex.biz", false, false, null);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with("biz").noargs();
    }
}
