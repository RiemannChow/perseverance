package com.riemann.interceptor;

import com.google.common.collect.Lists;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class SecurityInterceptor implements HandlerInterceptor {

    List<String> hasAuthUsernames = Lists.newArrayList();

    public void setHasAuthUsernames(List<String> hasAuthUsernames) {
        this.hasAuthUsernames = hasAuthUsernames;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(request.getParameter("arg2") + "执行权限拦截器");
        if (hasAuthUsernames.contains(request.getParameter("arg2"))) {
            return true;
        }
        return false;
    }

}
