package com.riemann.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 用户注册接口
     * @return
     */
    @RequestMapping("/register")
    public String register() {
        System.out.println("Register success!");
        return "Register success!";
    }

    /**
     * 验证注册身份证接口(需要调用公安户籍资源)
     * @return
     */
    @RequestMapping("/validateID")
    public String findResumeOpenState() {
        System.out.println("validateID");
        return "ValidateID success!";
    }

}
