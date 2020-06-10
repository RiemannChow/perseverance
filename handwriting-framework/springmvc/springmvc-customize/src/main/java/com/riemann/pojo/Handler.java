package com.riemann.pojo;

import com.google.common.collect.Maps;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 封装handler方法相关的信息
 */
@Data
public class Handler {

    private Object controller; // method.invoke(obj,);

    private Method method;

    private Pattern pattern; // spring中url是支持正则的

    private Map<String, Integer> paramIndexMapping; // 参数的顺序，是为了进行参数绑定。key是参数名，value是第几个参数

    public Handler(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
        paramIndexMapping = Maps.newHashMap();
    }

}
