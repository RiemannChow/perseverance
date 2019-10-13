package com.riemann.microserviceeurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class MicroserviceEurekaHaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceEurekaHaServerApplication.class, args);
    }

}
