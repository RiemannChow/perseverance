package com.riemann.microserviceproviderservicehi.client;

import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "microservice-provider-service-hi", fallbackFactory = FeignClientFallback.class)
public interface FeignClientServiceHi {

    @RequestMapping(value = "/hi",method = RequestMethod.GET)
    String sayHiFromFeignClient(@RequestParam(value = "name") String name);

}

/**
 * 回退类 FeignClientFallback 需实现 Feign Client 接口
 */
@Component
class FeignClientFallback implements FallbackFactory<FeignClientServiceHi> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeignClientFallback.class);

    // 检查回退原因
    public FeignClientServiceHi create(Throwable cause) {
        return new FeignClientServiceHi() {
            @Override
            public String sayHiFromFeignClient(String name) {
                // 日志最好放在各个fallback方法中，而不要直接放在create方法中。
                // 否则在引用启动时，就会打印该日志。
                LOGGER.info("fallback; reason was: ", cause);
                return "sorry " + name + ", system fallback!";
            }
        };
    }

}