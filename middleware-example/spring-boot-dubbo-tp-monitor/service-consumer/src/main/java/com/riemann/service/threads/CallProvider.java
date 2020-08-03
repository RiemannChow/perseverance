package com.riemann.service.threads;

import com.riemann.service.TpMonitorService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

@Component
public class CallProvider {

    @Reference
    TpMonitorService tpMonitorService;

    public void callMethod() {
        tpMonitorService.methodA();
        tpMonitorService.methodB();
        tpMonitorService.methodC();
    }

}
