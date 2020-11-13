package com.riemann.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "riemann-service-user")
public interface UserFeignClient {

    // 调用请求的路径
    @RequestMapping(value = "/api/user/info/{token}", method = RequestMethod.GET)
    public String info(@PathVariable(value = "token") String token);

}
