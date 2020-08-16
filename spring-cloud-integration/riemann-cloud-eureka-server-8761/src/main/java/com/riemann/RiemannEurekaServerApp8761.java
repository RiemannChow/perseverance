package com.riemann;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer // 声明当前项目为Eureka服务
public class RiemannEurekaServerApp8761 {
    public static void main(String[] args) {
        SpringApplication.run(RiemannEurekaServerApp8761.class,args);
    }
}
