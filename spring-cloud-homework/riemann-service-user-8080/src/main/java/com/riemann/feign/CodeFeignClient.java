package com.riemann.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "riemann-service-code")
public interface CodeFeignClient {

    // 调用请求的路径
    @RequestMapping(value = "/api/code/validate/{email}/{code}", method = RequestMethod.GET)
    public Integer validateCode(@PathVariable(value = "email") String email, @PathVariable(value = "code") String code);

}
