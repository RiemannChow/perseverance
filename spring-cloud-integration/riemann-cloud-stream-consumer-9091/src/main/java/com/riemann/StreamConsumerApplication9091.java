package com.riemann;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class StreamConsumerApplication9091 {

    public static void main(String[] args) {
        SpringApplication.run(StreamConsumerApplication9091.class,args);
    }

}
