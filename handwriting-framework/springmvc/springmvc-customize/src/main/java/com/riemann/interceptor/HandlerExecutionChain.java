package com.riemann.interceptor;

import com.google.common.collect.Lists;
import com.riemann.pojo.Handler;
import lombok.Data;

import java.util.List;

@Data
public class HandlerExecutionChain {

    // 执行链中的拦截器列表
    private List<HandlerInterceptor> handlerInterceptors = Lists.newArrayList();

    // 执行链中的handler
    private Handler handler;

    public HandlerExecutionChain(Handler handler) {
        this.handler = handler;
    }

}
