package com.riemann.client;

import com.riemann.dto.RpcRequest;
import com.riemann.dto.RpcResponse;
import com.riemann.handler.UserClientHandler;
import com.riemann.utils.JSONSerializer;
import com.riemann.utils.RpcDecoder;
import com.riemann.utils.RpcEncoder;
import com.riemann.zk.ZNode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.I0Itec.zkclient.ZkClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.*;

/**
 * 消费者
 */
public class RPCConsumer {

    // Zookeeper节点路径常量
    private static final String ZK_PATH = "/riemann-rpc-zk";

    // 创建一个线程池对象  -- 它要处理我们自定义事件
    private static ExecutorService executorService =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    // 声明一个自定义事件处理器map
    private static Map<String, UserClientHandler> userClientHandlers = new HashMap<>();

    private static Map<String, Channel> channels = new HashMap<>();

    private static EventLoopGroup eventLoopGroup;

    static {
        initClient();
    }

    // 创建一个代理对象
    public static Object createProxy(final Class<?> serviceClass, final String providerParam){
        System.out.println("createProxy before...");
        // 借助JDK动态代理生成代理对象
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{serviceClass}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setRequestId(String.valueOf(UUID.randomUUID()));
                rpcRequest.setClassName(serviceClass.getName());
                rpcRequest.setMethodName(method.getName());
                rpcRequest.setParameterTypes(method.getParameterTypes());
                rpcRequest.setParameters(args);
                System.out.println("request :" + rpcRequest);

                return sendMessageByFirstChannel(rpcRequest);
            }
        });
    }

    public static Object sendMessageByFirstChannel(RpcRequest rpcRequest) throws ExecutionException, InterruptedException {
        if (channels.size() < 1) {
            return null;
        }
        String key = getFirstKeyOfMap(channels);
        Channel channel = channels.get(key);
        UserClientHandler userClientHandler = userClientHandlers.get(key);
        userClientHandler.setChannel(channel);
        // 设置参数
        userClientHandler.setPara(rpcRequest);
        // 去服务端请求数据
        Object result = executorService.submit(userClientHandler).get();
        if (result.equals("timeout")) {
            // 消息超时，超时重发
            System.out.println("消息超时，重新发送到" + key);
            sendMessageByFirstChannel(rpcRequest);
        }
        return result;
    }

    // 初始化netty客户端
    public static void initClient() {
        try {
            connectToServerNodeList();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从Zookeeper上的 /riemann-rpc-zk 节点读取子节点，然后一一建立连接
     * 监听每一个子节点的删除事件
     * 监听 riemann-rpc-zk 的节点变动事件，如果新增了节点，就对其建立连接
     *
     * @throws InterruptedException
     */
    public static void connectToServerNodeList() throws InterruptedException {
        ZkClient zkClient = new ZkClient("47.113.82.141:2180");
        List<String> children = zkClient.getChildren(ZK_PATH);
        for (String childStr : children) {
            String childPath =  ZK_PATH + "/" + childStr;
            ZNode zNode = (ZNode)zkClient.readData(childPath);
            connectServer(zNode);
        }

        zkClient.subscribeChildChanges(ZK_PATH, (parentPath, currentChilds) -> {
            System.out.println(parentPath + " 's child changed, currentChilds:" + currentChilds);
            Set<String> connectedServers = channels.keySet();

            for (String server: connectedServers) {
                // 不存在就断开连接
                if (!currentChilds.contains(server)) {
                    disconnectServer(server);
                }
            }

            for (String child: currentChilds) {
                // 有新增的再次连接
                if (!connectedServers.contains(child)) {
                    ZNode znode = zkClient.readData(ZK_PATH + "/" + child);
                    connectServer(znode);
                }
            }
        });
    }

    public static void connectServer(ZNode zNode) throws InterruptedException {
        if (eventLoopGroup == null) {
            eventLoopGroup = new NioEventLoopGroup();
        }
        UserClientHandler userClientHandler = new UserClientHandler();
        userClientHandlers.put(getZNodeKey(zNode), userClientHandler);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new RpcEncoder(RpcRequest.class, new JSONSerializer()));
                        pipeline.addLast(new RpcDecoder(RpcResponse.class, new JSONSerializer()));
                        pipeline.addLast(userClientHandler);
                    }
                });
        System.out.println("与服务端" + zNode + " 建立连接");
        bootstrap.remoteAddress(zNode.getUrl(), zNode.getPort());
        bootstrap.connect().addListener((ChannelFuture future) -> {
            Channel channel = future.channel();
            final EventLoop eventLoop = channel.eventLoop();
            if (future.isSuccess()) {
                System.out.println(zNode + "连接成功");
                channels.put(getZNodeKey(zNode), channel);
            } else {
                System.out.println(zNode + "连接失败");
                future.cause().printStackTrace();
                eventLoop.schedule(() -> doConnect(bootstrap, zNode), 10, TimeUnit.SECONDS);
            }
        });
    }

    private static void doConnect(Bootstrap bootstrap, ZNode zNode) {
        try {
            if (bootstrap != null) {
                bootstrap.remoteAddress(zNode.getUrl(), zNode.getPort());
                ChannelFuture future = bootstrap.connect().addListener((ChannelFuture futureListener) -> {
                    final EventLoop eventLoop = futureListener.channel().eventLoop();
                    if (!futureListener.isSuccess()) {
                        // 连接tcp服务端不成功 10后重连
                        System.out.println(zNode.toString() + "与服务端" + getZNodeKey(zNode) + "断开连接!在10s之后准备尝试重连!");
                        eventLoop.schedule(() -> doConnect(bootstrap, zNode), 10, TimeUnit.SECONDS);
                    }
                });
                channels.put(zNode.getUrl() + ":" + zNode.getPort(), future.channel());
            }
        } catch (Exception e) {
            System.out.println("客户端连接失败!" + e.getMessage());
        }
    }

    public static void disconnectServer(String url) {
        if (!channels.containsKey(url)) {
            System.out.println("未连接该服务端");
            return;
        }
        System.out.println("关闭与服务端" + url + "的连接");
        Channel channel = channels.get(url);
        Future<?> shutdownFuture = channel.disconnect();
        ((ChannelFuture) shutdownFuture).addListener((future) -> {
            if (future.isSuccess()) {
                userClientHandlers.remove(url);
                channels.remove(url);
                System.out.println(url + "关闭成功");
            } else {
                System.out.println(url + "关闭失败");
                future.cause().printStackTrace();
            }
        });
    }

    public static String getZNodeKey(ZNode zNode) {
        return zNode.getUrl() + ":" + zNode.getPort();
    }

    public static String getFirstKeyOfMap(Map<String, Channel> map) {
        return (String) map.keySet().toArray()[0];
    }

}
