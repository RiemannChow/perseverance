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
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

/**
 * 消费者
 */
public class RPCConsumer {

    // Zookeeper节点路径常量
    private static final String ZK_PATH = "/riemann-rpc-zk";

    public static ZkClient zkClient = new ZkClient("47.113.82.141:2180");

    // 创建一个线程池对象  -- 它要处理我们自定义事件
    private static ExecutorService executorService =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    // 声明一个自定义事件处理器map
    private static Map<String, UserClientHandler> userClientHandlers = new HashMap<>();

    private static Map<String, Channel> channels = new HashMap<>();

    private static EventLoopGroup eventLoopGroup;

    // 优先队列比较器，对节点根据响应时长进行排序，响应时长的排在前面。
    static Comparator<ZNode> cmp = new Comparator<ZNode>() {
        @Override
        public int compare(ZNode o1, ZNode o2) {
            return (int)o1.getLastResponseDuration() - (int)o2.getLastResponseDuration();
        }
    };
    private static Queue<ZNode> connectedServerQueue = new PriorityQueue<>(cmp);

    static {
        initClient();
    }

    // 创建一个代理对象
    public static Object createProxy(final Class<?> serviceClass, final String providerParam){
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
        String key = getFirstKeyOfQueue();
        if (key == null) {
            return null;
        }
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
        // 每次请求结束之后重新排序
        resetQueue();
        System.out.println("重新排序之后：" + Arrays.toString(connectedServerQueue.toArray()));
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
        List<String> children = zkClient.getChildren(ZK_PATH);
        for (String childStr : children) {
            connectServer(childStr);
        }

        // 订阅父节点，监听新增的子节点
        zkClient.subscribeChildChanges(ZK_PATH, (parentPath, currentChilds) -> {
            System.out.println(parentPath + "的子节点变更为：" + currentChilds);
            Set<String> connectedServers = channels.keySet();

            for (String child: currentChilds) {
                // 有新增的则连接
                if (!connectedServers.contains(child)) {
                    connectServer(child);
                }
            }
        });
    }

    public static void connectServer(String childStr) throws InterruptedException {
        String childPath =  ZK_PATH + "/" + childStr;
        ZNode zNode = zkClient.readData(childPath);
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
                connectedServerQueue.add(zNode);
                System.out.println("连接完毕：" + Arrays.toString(connectedServerQueue.toArray()));
            } else {
                System.out.println(zNode + "连接失败");
                future.cause().printStackTrace();
                eventLoop.schedule(() -> doConnect(bootstrap, zNode), 10, TimeUnit.SECONDS);
            }
        });

        // 订阅当前节点，监听当前节点数据变化和删除
        zkClient.subscribeDataChanges(childPath, new IZkDataListener() {
            @Override
            public void handleDataChange(String str, Object node) throws Exception {
                ZNode changedNode = (ZNode)node;
                connectedServerQueue.removeIf((obj) -> {
                    boolean result = obj.getUrl().equals(changedNode.getUrl()) && obj.getPort().equals(changedNode.getPort());
                    Instant lastResponseTime = obj.getLastResponseTime();
                    return result;
                });
                connectedServerQueue.add(changedNode);
                System.out.println("节点变更后：" + Arrays.toString(connectedServerQueue.toArray()));
            }

            @Override
            public void handleDataDeleted(String server) throws Exception {
                // 节点被删除则断开连接
                disconnectServer(server);
                System.out.println("节点删除后：" + Arrays.toString(connectedServerQueue.toArray()));
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
                connectedServerQueue.removeIf((obj) -> {
                    return (obj.getUrl() + ":" + obj.getPort()).equals(url);
                });
                System.out.println(Arrays.toString(connectedServerQueue.toArray()));
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

    // 重排序，超过5s的请求时间清零
    public static void resetQueue() {
        Queue<ZNode> needResetQueue = new PriorityQueue<>(cmp);
        Object[] zNodes = connectedServerQueue.toArray();
        for (Object object : zNodes) {
            ZNode node = (ZNode)object;
            if (null != node.getLastResponseTime() && Duration.between(node.getLastResponseTime(), Instant.now()).getSeconds() > 5) {
                node.setLastResponseTime(null);
                node.setLastResponseDuration(0);
            }
            needResetQueue.add(node);
        }

        connectedServerQueue = needResetQueue;
    }

    public static String getFirstKeyOfQueue() {
        ZNode node = connectedServerQueue.peek();
        // 取出第一个，但是不弹出，所以用peek而不是poll
        if (node == null) {
            return null;
        }

        return node.getUrl() + ":" + node.getPort();
    }

    public static String getRandomKeyOfMap(Map<String, Channel> map) {
        Set<String> strings = map.keySet();
        Random random = new Random();
        int index = random.nextInt(strings.size());
        return (String)strings.toArray()[index];
    }

}
