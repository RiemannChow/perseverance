package com.riemann;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ScaCodeApplication8081 {

    public static void main(String[] args) {
        SpringApplication.run(ScaCodeApplication8081.class, args);
    }

}
