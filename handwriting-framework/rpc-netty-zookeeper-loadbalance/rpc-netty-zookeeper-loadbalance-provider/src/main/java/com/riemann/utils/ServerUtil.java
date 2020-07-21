package com.riemann.utils;

import com.riemann.zk.ZNode;
import org.I0Itec.zkclient.ZkClient;

import java.time.Instant;
import java.util.Random;

public class ServerUtil {

    public static String url = "127.0.0.1";
    public static int port = 0;
    public static String ZK_PATH = "/riemann-rpc-zk";
    private static ZkClient zkClient;

    static {
        zkClient = new ZkClient("47.113.82.141:2180");
        if (!zkClient.exists(ZK_PATH)){
            zkClient.createPersistent(ZK_PATH);
        }
        port = getRandomPort();
    }

    // 注册本服务到Zookeeper
    public static void registerServer() {
        ZNode zNode = new ZNode();
        zNode.setUrl(url);
        zNode.setPort(port);
        zkClient.createEphemeral(ZK_PATH + "/" + url + ":" + port, zNode);
    }

    // 更新本服务的最后响应时间
    public static void updateLastResponseDuration(long duration) {
        System.out.println("响应时间：" + duration + "s");
        String path = ZK_PATH + "/" + url + ":" + port;
        ZNode zNode = new ZNode();
        zNode.setUrl(url);
        zNode.setPort(port);
        zNode.setLastResponseTime(Instant.now());
        zNode.setLastResponseDuration(duration);
        if (!zkClient.exists(path)) {
            zkClient.createEphemeral(path, zNode);
        } else {
            zkClient.writeData(path, zNode);
        }
    }

    // 随机获取8000-9000的端口
    public static int getRandomPort(){
        Random random = new Random();
        int port = random.nextInt(1000) + 8000;
        return port;
    }

}
