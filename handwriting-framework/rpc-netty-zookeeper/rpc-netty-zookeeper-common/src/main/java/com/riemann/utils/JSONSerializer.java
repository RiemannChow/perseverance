package com.riemann.utils;

import com.alibaba.fastjson.JSON;

import java.io.IOException;

/**
 * 采用JSON的方式，定义JSONSerializer的实现类
 */
public class JSONSerializer implements Serializer {

    public byte[] serialize(Object object) throws IOException {
        return JSON.toJSONBytes(object);
    }

    public <T> T deserialize(Class<T> clazz, byte[] bytes) throws IOException {
        return JSON.parseObject(bytes, clazz);
    }

}
