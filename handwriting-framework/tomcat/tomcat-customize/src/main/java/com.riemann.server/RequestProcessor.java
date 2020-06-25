package com.riemann.server;

import com.riemann.server.servlet.HttpServlet;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

@Data
@AllArgsConstructor
public class RequestProcessor extends Thread {

    private Socket socket;

    private Map<String, HttpServlet> servletMap;

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();

            // 封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());

            HttpServlet httpServlet = servletMap.get(request.getUrl());
            // 静态资源处理
            if (httpServlet == null) {
                response.outputHtml(request.getUrl());
            } else {
                // 动态资源servlet请求
                httpServlet.service(request, response);
            }

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
