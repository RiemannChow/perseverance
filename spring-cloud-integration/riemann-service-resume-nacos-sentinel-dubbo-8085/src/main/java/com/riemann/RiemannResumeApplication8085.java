package com.riemann;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EntityScan("com.riemann.pojo")
@EnableDiscoveryClient // 开启注册中心客户端 （通用型注解，比如注册到Eureka、Nacos等）
                       // 说明：从SpringCloud的Edgware版本开始，不加注解也ok，但是建议大家加上
public class RiemannResumeApplication8085 {

    public static void main(String[] args) {
        SpringApplication.run(RiemannResumeApplication8085.class, args);
    }

}
