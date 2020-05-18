package com.riemann.rpc.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class RpcServer {

    // 注册表
    // key:业务接口名；value:实现类的实例
    private Map<String, Object> registerMap = Maps.newHashMap();

    // 用于缓存指定包下的业务接口实现类类名
    private List<String> classCache = Lists.newArrayList();

    public void publish(String basePackage) throws Exception {
        getProviderClass(basePackage);
        doRegister();
    }

    private void doRegister() throws Exception {
        if (classCache.size() == 0) return;
        for (String className : classCache) {
            Class<?> clazz = Class.forName(className);
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces.length == 1) {
                registerMap.put(interfaces[0].getName(), clazz.newInstance());
            }
        }
    }

    public void getProviderClass(String basePackage) {
        // 获取指定包目录中的资源
        URL resource = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "/"));
        // 若目录没有任何资源，则直接结束
        if (resource == null) return;
        // 将URL资源转换为File
        File dir = new File(resource.getFile());
        // 遍历指定包及其子包中的所有文件，查找 .class 文件
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                // 递归
                getProviderClass(basePackage + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String fileName = file.getName().replace(".class", "").trim();
                // 将实现类的全限定性类名写入缓存
                classCache.add(basePackage + "." + fileName);
            }
        }
    }

    public void start() throws Exception {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parentGroup, childGroup)
                    // 指定用于存放连接请求的长度，不写的话默认50
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    // 指定启用心跳机制来检测长连接的存活性
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new ObjectEncoder());
                            pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            pipeline.addLast(new RpcServerHandler(registerMap));
                        }
                    });
            // 转为同步，因为异步的话，此操作还没执行完，有可能下面就已经关闭了。
            ChannelFuture future = bootstrap.bind(8888).sync();
            System.out.println("服务器已启动，端口号为：8888");
            future.channel().closeFuture().sync();
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }

}
