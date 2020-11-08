package com.riemann;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer  // 声明为EurekaServer服务
public class EurekaServerApplication9001 {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication9001.class, args);
    }

}
