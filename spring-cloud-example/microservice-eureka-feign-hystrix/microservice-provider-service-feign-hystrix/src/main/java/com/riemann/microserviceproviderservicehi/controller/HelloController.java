package com.riemann.microserviceproviderservicehi.controller;

import com.riemann.microserviceproviderservicehi.client.FeignClientServiceHi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    FeignClientServiceHi feignClientServiceHi;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @GetMapping(value = "/hi")
    public String hi(@RequestParam String name) {
        return feignClientServiceHi.sayHiFromFeignClient(name);
    }

    @GetMapping(value = "/log-instance")
    public void logUserInstance() {
        ServiceInstance serviceInstance = this.loadBalancerClient.choose("microservice-provider-service-hi");
        // 打印当前选择的是哪个节点
        LOGGER.info("{}:{}:{}", serviceInstance.getServiceId(), serviceInstance.getHost(), serviceInstance.getPort());
    }

}
