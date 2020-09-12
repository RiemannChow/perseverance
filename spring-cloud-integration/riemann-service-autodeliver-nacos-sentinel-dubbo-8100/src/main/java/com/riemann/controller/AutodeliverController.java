package com.riemann.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.riemann.config.SentinelFallbackClass;
import com.riemann.service.ResumeService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/autodeliver")
public class AutodeliverController {

    // 引入dubbo的注解，远程RPC调用
    @Reference
    private ResumeService resumeService;

    @GetMapping("/checkState/{userId}")
    @SentinelResource(value = "findResumeOpenState", blockHandlerClass = SentinelFallbackClass.class,
            blockHandler = "handleException", fallback = "handleError", fallbackClass = SentinelFallbackClass.class)
    public Integer findResumeOpenState(@PathVariable Long userId) {
        return resumeService.findDefaultResumeByUserId(userId);
    }

}
