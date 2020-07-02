package com.riemann.server;

import lombok.Data;

import java.io.IOException;
import java.io.InputStream;

/**
 * 把请求信息封装为Request对象（根据InputSteam输入流封装）
 */
@Data
public class Request {

    private String method; // 请求方式，比如GET/POST

    private String url;  // 例如 /,/index.html

    private InputStream inputStream;  // 输入流，其他属性从输入流中解析出来

    /**
     * 构造器，输入流传入
     * @param inputStream
     */
    public Request(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;

        // 从输入流中获取请求信息
        // 从输入流中获取请求信息
        int count = 0;
        while (count == 0) {
            count = inputStream.available();
        }

        byte[] bytes = new byte[count];
        inputStream.read(bytes);

        String inputStr = new String(bytes);
        // 获取第一行请求头信息
        String firstLine = inputStr.split("\\n")[0]; // GET / HTTP/1.1
        String[] strings = firstLine.split(" ");
        this.method = strings[0];
        this.url = strings[1];

        System.out.println("method: " + method);
        System.out.println("url: " + url);

    }

}
