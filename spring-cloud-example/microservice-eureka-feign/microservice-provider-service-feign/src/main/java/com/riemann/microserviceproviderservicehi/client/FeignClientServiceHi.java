package com.riemann.microserviceproviderservicehi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "microservice-provider-service-hi")
public interface FeignClientServiceHi {

    @RequestMapping(value = "/hi",method = RequestMethod.GET)
    String sayHiFromFeignClient(@RequestParam(value = "name") String name);

}
