package com.riemann.server.servlet;

import com.riemann.server.Request;
import com.riemann.server.Response;
import com.riemann.server.utils.HttpProtocolUtil;

import java.io.IOException;

public class RiemannServlet extends HttpServlet {

    @Override
    public void doGet(Request request, Response response) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String content = "<h1>RiemannServlet get</h1>";
        try {
            response.output((HttpProtocolUtil.getHttpHeader200(content.getBytes().length) + content));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(Request request, Response response) {
        String content = "<h1>RiemannServlet post</h1>";
        try {
            response.output((HttpProtocolUtil.getHttpHeader200(content.getBytes().length) + content));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public void destory() throws Exception {

    }

}
