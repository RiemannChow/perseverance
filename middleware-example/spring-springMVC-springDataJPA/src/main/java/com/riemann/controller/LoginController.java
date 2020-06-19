package com.riemann.controller;

import com.riemann.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {
    /**
     * 向用户登录页面跳转
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String toLogin() {
        return "login";
    }

    /**
     * 默认跳转到列表页
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "redirect:queryAll";
    }

    /**
     * 用户登录
     *
     * @param user
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(User user, Model model, HttpServletRequest request) {
        System.out.println("开始登陆");
        //获取用户名和密码
        String username = user.getUsername();
        String password = user.getPassword();
        System.out.println("username：" + username + ",password:" + password);
        //些处横板从数据库中获取对用户名和密码后进行判断
        if (username != null && username.equals("admin") && password != null && password.equals("admin")) {
            model.addAttribute(user);
            //将用户对象添加到Session中
            request.getSession(true).setAttribute("USER_SESSION", user);
            return "redirect:queryAll";
        }
        model.addAttribute("msg", "用户名或密码错误，请重新登录！");
        System.out.println("用户名或密码错误，请重新登录！");
        return "login";
    }

}