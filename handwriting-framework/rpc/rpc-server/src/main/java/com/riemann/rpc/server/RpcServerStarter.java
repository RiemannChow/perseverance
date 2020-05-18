package com.riemann.rpc.server;

public class RpcServerStarter {

    public static void main(String[] args) throws Exception {
        RpcServer server = new RpcServer();
        server.publish("com.riemann.rpc.service");
        server.start();
    }

}
