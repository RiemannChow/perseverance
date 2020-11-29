package com.riemann;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
public class ScaGateWayApplication9002 {

    public static void main(String[] args) {
        SpringApplication.run(ScaGateWayApplication9002.class, args);
    }

}
