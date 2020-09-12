package com.riemann.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.riemann.config.SentinelFallbackClass;
import com.riemann.controller.service.ResumeServiceFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/autodeliver")
public class AutodeliverController {

    @Autowired
    private ResumeServiceFeignClient resumeServiceFeignClient;


    /**
     * @SentinelResource注解类似于Hystrix中的@HystrixCommand注解
     *
     * value:定义资源名
     * blockHandlerClass:指定Sentinel规则异常兜底逻辑所在class类
     * blockHandler:指定Sentinel规则异常兜底逻辑具体哪个方法
     * fallbackClass:指定Java运行时异常兜底逻辑所在class类
     * fallback:指定Java运行时异常兜底逻辑具体哪个方法
     */
    @GetMapping("/checkState/{userId}")
    @SentinelResource(value = "findResumeOpenState", blockHandlerClass = SentinelFallbackClass.class, blockHandler = "handleException", fallback = "handleError", fallbackClass = SentinelFallbackClass.class)
    public Integer findResumeOpenState(@PathVariable Long userId) {
        // 模拟降级:异常比例
        int i = 1/0;
        Integer defaultResumeState = resumeServiceFeignClient.findDefaultResumeState(userId);
        return defaultResumeState;
    }

}
