package com.riemann;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;


@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigServer  // 开启配置中心功能
public class ConfigServerApplication9006 {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication9006.class,args);
    }

}
