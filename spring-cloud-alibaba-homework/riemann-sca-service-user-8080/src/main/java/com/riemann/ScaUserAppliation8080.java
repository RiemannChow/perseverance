package com.riemann;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ScaUserAppliation8080 {

    public static void main(String[] args) {
        SpringApplication.run(ScaUserAppliation8080.class, args);
    }

}