package com.riemann;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EntityScan("com.riemann.pojo")
public class OauthServerApplication9999 {

    public static void main(String[] args) {
        SpringApplication.run(OauthServerApplication9999.class,args);
    }

}
