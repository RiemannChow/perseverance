package com.riemann.microserviceproviderservicehi;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Configuration;

/**
 * 使用 RibbonClient，为特定 name的 Ribbon Client 自定义配置。
 * 使用 @RibbonClient 的 configuration 属性，指定 Ribbon 的配置类
 */
@Configuration
@RibbonClient(name = "microservice-provider-service-hi", configuration = RibbonConfiguration.class)
public class TestConfiguration {}
