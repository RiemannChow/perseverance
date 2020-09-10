package com.riemann;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import zipkin2.server.internal.EnableZipkinServer;

import javax.sql.DataSource;

@SpringBootApplication
@EnableZipkinServer // 开启Zipkin 服务器功能
public class ZipkinServerApplication9411 {

    public static void main(String[] args) {
        SpringApplication.run(ZipkinServerApplication9411.class,args);
    }

    // 注入事务控制器
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}
