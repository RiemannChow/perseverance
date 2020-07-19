package com.riemann;

import com.riemann.service.UserServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerBoot {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(ServerBoot.class, args);
        // 启动服务器
        UserServiceImpl.startServer("127.0.0.1", 8999);
    }

}
