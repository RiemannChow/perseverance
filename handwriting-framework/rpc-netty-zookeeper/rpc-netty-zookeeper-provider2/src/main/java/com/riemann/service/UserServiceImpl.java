package com.riemann.service;

import com.riemann.dto.RpcRequest;
import com.riemann.dto.RpcResponse;
import com.riemann.handler.UserServiceHandler;
import com.riemann.utils.JSONSerializer;
import com.riemann.utils.RpcDecoder;
import com.riemann.utils.RpcEncoder;
import com.riemann.zk.ZNode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserServiceImpl implements IUserService {

    private static final String ZK_PATH = "/riemann-rpc-zk";

    static {
        String url = "127.0.0.1";
        int port = getRandomPort();
        try {
            startServer(url, port);
            System.out.println("Server started on " + url + ":" + port);
            ZkClient zkClient = new ZkClient("47.113.82.141:2180");
            ZNode zNode = new ZNode();
            zNode.setUrl(url);
            zNode.setPort(port);
            if (!zkClient.exists(ZK_PATH)) {
                zkClient.createPersistent(ZK_PATH);
            }
            zkClient.createEphemeral(ZK_PATH + "/" + url + ":" + port, zNode);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 将来客户端要远程调用的方法
    public String sayHello(String msg) {
        System.out.println("hello " + msg);
        return "success";
    }

    // 创建一个方法启动服务器
    public static void startServer(String hostName, int port) throws InterruptedException {
        // 1.创建两个线程池对象
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        // 2.创建服务端的启动引导对象
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        // 3.配置启动引导对象
        serverBootstrap.group(bossGroup, workGroup)
                // 设置通道为NIO
                .channel(NioServerSocketChannel.class)
                // 创建监听channel
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        // 获取管道对象
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        // 给管道对象pipLine 设置编码
                        pipeline.addLast(new RpcDecoder(RpcRequest.class, new JSONSerializer()));
                        pipeline.addLast(new RpcEncoder(RpcResponse.class, new JSONSerializer()));

                        // 把我们自定义的一个ChannelHandler添加到通道中
                        pipeline.addLast(new UserServiceHandler());
                    }
                });

        // 4.绑定端口
        serverBootstrap.bind(hostName, port).sync();

        System.out.println("rpc-provider已启动...");
    }

    // 随机获取8000-9000的端口
    public static int getRandomPort(){
        Random random = new Random();
        int port = random.nextInt(1000) + 8000;
        return port;
    }

}
