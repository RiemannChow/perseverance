package com.riemann.client;

import com.riemann.dto.RpcRequest;
import com.riemann.handler.UserClientHandler;
import com.riemann.utils.JSONSerializer;
import com.riemann.utils.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 消费者
 */
public class RPCConsumer {

    // 1.创建一个线程池对象  -- 它要处理我们自定义事件
    private static ExecutorService executorService =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    // 2.声明一个自定义事件处理器  UserClientHandler
    private static UserClientHandler userClientHandler;

    // 3.编写方法，初始化客户端 （创建连接池、bootStrap、设置bootStrap、连接服务器）
    public static void initClient() throws InterruptedException {
        // 1) 初始化 userClientHandler
        userClientHandler = new UserClientHandler();
        // 2) 创建连接池对象
        EventLoopGroup group = new NioEventLoopGroup();
        // 3) 创建客户端的引导对象
        Bootstrap bootstrap = new Bootstrap();
        // 4) 配置启动引导对象
        bootstrap.group(group)
                // 设置通道为NIO
                .channel(NioSocketChannel.class)
                // 设置请求协议为TCP
                .option(ChannelOption.TCP_NODELAY, true)
                // 监听channel 并初始化
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        // 获取 ChannelPipeline
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        // 设置编码
                        // pipeline.addLast(new StringEncoder());
                        // pipeline.addLast(new StringDecoder());

                        pipeline.addLast(new RpcEncoder(RpcRequest.class, new JSONSerializer()));
                        pipeline.addLast(new StringDecoder());
                        // 添加自定义事件处理器
                        pipeline.addLast(userClientHandler);
                    }
                });
        // 5) 连接服务端
        bootstrap.connect("127.0.0.1", 8999).sync();
        System.out.println("rpc-consumer已启动...");
    }

    // 4.编写一个方法，使用JDK的动态代理创建对象
    /**
     *
     * @param serviceClass   接口类型，根据哪个接口生成子类代理对象
     * @param providerParam  "UserService#sayHello#riemann"
     * @return
     */
    public static Object createProxy(Class<?> serviceClass, final String providerParam) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{serviceClass}, new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 1.初始化客户端client
                        if (userClientHandler == null) {
                            initClient();
                        }

                        String requestId = UUID.randomUUID().toString();
                        String className = method.getDeclaringClass().getName();
                        String methodName = method.getName();
                        Class<?>[] parameterTypes = method.getParameterTypes();

                        RpcRequest rpcRequest = new RpcRequest();
                        rpcRequest.setRequestId(requestId);
                        rpcRequest.setClassName(className);
                        rpcRequest.setMethodName(methodName);
                        rpcRequest.setParameterTypes(parameterTypes);
                        rpcRequest.setParameters(args);

                        // 2.给 userClientHandler 设置param参数
                        // userClientHandler.setParam(providerParam + args[0]);
                        userClientHandler.setParam(rpcRequest);

                        // 3.使用线程池，开启一个线程处理call()写操作，并返回结果。
                        Object result = executorService.submit(userClientHandler).get();

                        // 4.return 结果
                        return result;
                    }
                });
    }

}
