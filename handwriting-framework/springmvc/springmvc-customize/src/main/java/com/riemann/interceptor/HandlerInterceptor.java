package com.riemann.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HandlerInterceptor {

    // 在handler业务执行之前执行，如果返回false，则不执行
    boolean preHandle(HttpServletRequest request, HttpServletResponse response);

}
