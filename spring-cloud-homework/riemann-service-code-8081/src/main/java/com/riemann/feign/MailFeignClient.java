package com.riemann.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "riemann-service-mail")
public interface MailFeignClient {

    // 调用请求的路径
    @RequestMapping(value = "/api/email/{email}/{code}", method = RequestMethod.GET)
    public boolean sendCode(@PathVariable(value = "email") String email, @PathVariable(value = "code") String code);

}
