package com.riemann.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

/**
 * 自定义事件处理器
 */
public class UserClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    // 1.定义成员变量
    private ChannelHandlerContext context; // 事件处理器上下文对象（存储handler写信息，写操作）
    private String result; // 记录服务器返回的数据
    private Object param; // 记录将要返回给服务器的数据

    // 2.实现 channelActive 客户端和服务器连接时，该方法就自动执行。
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务器已连接...");
        // 初始化 ChannelHandlerContext
        this.context = ctx;
    }


    // 3.实现 channelRead 当我们读到服务器数据，该方法自动执行。
    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 将读到的服务器的数据msg,设置为成员变量的值
        result = msg.toString();
        notify();
    }

    // 4.将客户端的数据写到服务器
    public synchronized Object call() throws Exception {
        // context 给服务器写数据
        context.writeAndFlush(param);
        wait();
        return result;
    }

    // 5.设置参数的方法
    public void setParam(Object param) {
        this.param = param;
    }

}
