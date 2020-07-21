package com.riemann.handler;

import com.alibaba.fastjson.JSON;
import com.riemann.dto.RpcRequest;
import com.riemann.dto.RpcResponse;
import com.riemann.utils.ServerUtil;
import com.riemann.utils.SpringContextUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * 自定义的业务处理器
 */
@Component
public class UserServiceHandler extends ChannelInboundHandlerAdapter {

    // 当客户端读取数据时，该方法会被调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Instant startInstant = Instant.now();

        System.out.println("接收到客户端信息：" + JSON.toJSON(msg).toString());
        // 将读到的msg对象，强转成RpcRequest对象
        RpcRequest rpcRequest = (RpcRequest) msg;
        // 加载class文件
        Class<?> clazz = Class.forName(rpcRequest.getClassName());
        // 通过class获取服务器spring容器中service类的实例化bean对象
        Object serviceBean = SpringContextUtil.getBean(clazz);
        Method method = clazz.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
        Object result = method.invoke(serviceBean, rpcRequest.getParameters());

        System.out.println(LocalDateTime.now().toLocalTime() + ":" + rpcRequest.toString());
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());
        rpcResponse.setResult(result);
        // 服务器写入数据，将结果返回给客户端
        ctx.writeAndFlush(rpcResponse);

        Instant endInstant = Instant.now();
        long seconds = Duration.between(startInstant, endInstant).getSeconds();
        ServerUtil.updateLastResponseDuration(seconds);
    }

}
