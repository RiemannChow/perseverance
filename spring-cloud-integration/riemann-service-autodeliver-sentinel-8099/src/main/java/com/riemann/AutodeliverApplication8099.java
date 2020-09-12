package com.riemann;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients  // 开启Feign 客户端功能
public class AutodeliverApplication8099 {

    public static void main(String[] args) {
        SpringApplication.run(AutodeliverApplication8099.class,args);
    }

}
