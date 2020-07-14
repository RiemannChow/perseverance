package com.riemann.handler;

import com.alibaba.fastjson.JSON;
import com.riemann.dto.RpcRequest;
import com.riemann.utils.SpringContextUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 自定义的业务处理器
 */
@Component
public class UserServiceHandler extends ChannelInboundHandlerAdapter {

    // 当客户端读取数据时，该方法会被调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("接收到客户端信息：" + JSON.toJSON(msg).toString());
        // 将读到的msg对象，强转成RpcRequest对象
        RpcRequest rpcRequest = (RpcRequest) msg;
        // 加载class文件
        Class<?> clazz = Class.forName(rpcRequest.getClassName());
        // 通过class获取服务器spring容器中service类的实例化bean对象
        Object serviceBean = SpringContextUtil.getBean(clazz);
        Method method = clazz.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
        method.invoke(serviceBean, rpcRequest.getParameters());

        //服务器写入数据，将结果返回给客户端
        ctx.writeAndFlush("success");

        // 注意：客户端将来发送请求的时候会传递一个参数：UserService#sayHello#riemann

        // 1.判断当前的请求是否符合规则
        /*if (msg.toString().startsWith("UserService")) {
            // 2.如何符合规则，调用实现类获取到一个result
            UserServiceImpl userService = new UserServiceImpl();
            String result = userService.sayHello(msg.toString().substring(msg.toString().lastIndexOf("#") + 1));
            // 3.将调用实现类的方法获得的结果写到客户端
            ctx.writeAndFlush(result);
        }*/
    }

}
