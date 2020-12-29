package com.riemann.rabbitmq.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class RabbitmqOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(RabbitmqOrderApplication.class, args);
    }
}
