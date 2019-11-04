package com.riemann.microserviceproviderservicehi;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Configuration;

@Configuration
@RibbonClient(name = "microservice-provider-service-hi", configuration = RibbonConfiguration.class)
public class TestConfiguration {
}
