package com.riemann.handler;

import com.riemann.dto.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;

/**
 * 自定义事件处理器
 */
@Data
@ChannelHandler.Sharable
public class UserClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private String result;
    private Object para;
    private Channel channel;

    /**
     * 收到服务端数据，唤醒等待线程
     */
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 返回的是RpcResponse类型
        RpcResponse rpcResponse = (RpcResponse)msg;
        result = rpcResponse.getResult().toString();
        notify();
    }

    /**
     * 写出数据，开始等待唤醒
     */
    public synchronized Object call() throws InterruptedException {
        if (channel == null) {
            return null;
        }
        System.out.println("发送消息到服务端" + channel.remoteAddress());
        channel.writeAndFlush(para);
        Instant startInstant = Instant.now();
        // 超时时间10s
        long waitTime = 10000;
        wait(waitTime);
        Instant endInstance = Instant.now();
        // 如果超时了，就返回一个timeout
        if (Duration.between(startInstant, endInstance).getSeconds() >= waitTime / 1000) {
            System.out.println("返回超时");
            return "timeout";
        }
        return result;
    }

}
