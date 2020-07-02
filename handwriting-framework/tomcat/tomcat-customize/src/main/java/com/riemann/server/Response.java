package com.riemann.server;

import com.riemann.utils.HttpProtocolUtil;
import com.riemann.utils.StaticResourceUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 封装Response对象，需要依赖于OutputStream
 *
 * 该对象需要提供核心方法，输出html
 */
@Data
@AllArgsConstructor
public class Response {

    private OutputStream outputStream;

    /**
     * 使用输出流输出指定字符串
     * @param content
     * @throws IOException
     */
    public void output(String content) throws IOException {
        outputStream.write(content.getBytes());
    }

    /**
     * 输出html格式
     * @param path url 随后要根据url来获取到静态资源的绝对路径，进一步根据绝对路径读取该静态资源文件，最终通过输出流输出。
     *                 / -----> classes
     * @throws IOException
     */
    public void outputHtml(String path) throws IOException {
        // 获取静态资源文件的绝对路径
        String absoluteResourcePath = StaticResourceUtil.getAbsolutePath(path);

        // 输入静态资源文件
        File file = new File(absoluteResourcePath);
        if (file.exists() && file.isFile()) {
            // 读取静态资源文件，输出静态资源
            StaticResourceUtil.outputStaticResource(new FileInputStream(file), outputStream);
        } else {
            // 输出404
            output(HttpProtocolUtil.getHttpHeader404());
        }
    }

}
