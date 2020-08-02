package com.riemann.controller;

import com.riemann.service.IpPrintServiceB;
import com.riemann.service.IpPrintServiceC;
import com.riemann.utils.ThreadLocalUtil;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class IndexController {

    @Reference
    IpPrintServiceB ipPrintServiceB;

    @Reference
    IpPrintServiceC ipPrintServiceC;

    @RequestMapping("/")
    public String indexPage(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        // 将客户端的ip放到ThreadLocal中
        ThreadLocalUtil.put(remoteAddr);
        ipPrintServiceB.printIp();
        ipPrintServiceC.printIp();
        return "Hello Dubbo";
    }

}
