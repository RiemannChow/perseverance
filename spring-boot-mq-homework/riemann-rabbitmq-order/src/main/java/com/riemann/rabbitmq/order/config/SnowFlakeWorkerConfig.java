package com.riemann.rabbitmq.order.config;

import com.riemann.rabbitmq.order.util.SnowFlakeWorkerUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SnowFlakeWorkerConfig {
    @Bean
    public SnowFlakeWorkerUtil snowFlakeWorkerUtil() {
        return new SnowFlakeWorkerUtil(1, 1);
    }
}