package com.riemann.interceptor;

import com.riemann.pojo.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 拦截器
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        //获取请求的RUi:去除http:localhost:8080这部分剩下的
        String uri = request.getRequestURI();
        System.out.println(uri);
        //UTL:除了login.jsp是可以公开访问的，其他的URL都进行拦截控制
        if (uri.indexOf("/login") >= 0) {
            return true;
        }
        //获取session
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("USER_SESSION");
        //判断session中是否有用户数据，如果有，则返回true，继续向下执行
        if (user != null) {
            return true;
        }
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        System.out.println("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        System.out.println("afterCompletion");
    }

}