package com.riemann.service.impl;

import com.riemann.service.IpPrintServiceB;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.rpc.RpcContext;

@Service
public class IpPrintServiceBImpl implements IpPrintServiceB {

    @Override
    public String printIp() {
        String clientIp = RpcContext.getContext().getAttachment("clientIp");
        System.out.println(clientIp);
        return clientIp;
    }

}
