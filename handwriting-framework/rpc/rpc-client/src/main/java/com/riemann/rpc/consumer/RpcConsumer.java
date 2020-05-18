package com.riemann.rpc.consumer;

import com.riemann.rpc.service.SomeService;
import com.riemann.rpc.client.RpcProxy;

public class RpcConsumer {

    public static void main(String[] args) {
        SomeService service = RpcProxy.create(SomeService.class);
        // 远程方法调用
        System.out.println(service.hello("riemann"));
        // 本地方法调用
        System.out.println(service.hashCode());
    }

}
