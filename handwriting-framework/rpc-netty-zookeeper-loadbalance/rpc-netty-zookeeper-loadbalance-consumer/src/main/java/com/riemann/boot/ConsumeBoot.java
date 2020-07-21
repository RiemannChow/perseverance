package com.riemann.boot;

import com.riemann.client.RPCConsumer;
import com.riemann.service.IUserService;

public class ConsumeBoot {

    // 参数定义
    private static final String PROVIDE_NAME = "UserService#sayHello#";

    public static void main(String[] args) throws InterruptedException {
        // 1.创建代理对象
        IUserService service = (IUserService) RPCConsumer.createProxy(IUserService.class, PROVIDE_NAME);

        // 2.循环给服务器写数据
        while (true) {
            Thread.sleep(5000);
            String result = service.sayHello("riemann");
            System.out.println(result);
        }
    }

}
