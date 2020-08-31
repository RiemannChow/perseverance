package com.riemann;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

@SpringBootApplication
@EnableDiscoveryClient
@EnableTurbine  // 开启Turbine聚合功能
public class HystrixTurbineApplication9001 {

    public static void main(String[] args) {
        SpringApplication.run(HystrixTurbineApplication9001.class,args);
    }

}
