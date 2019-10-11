package com.riemann.microserviceeurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class MicroserviceEurekaAuthenticatingServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceEurekaAuthenticatingServerApplication.class, args);
    }

}
