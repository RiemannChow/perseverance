package com.riemann.controller;

import com.riemann.feign.CodeFeignClient;
import com.riemann.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    CodeFeignClient codeFeignClient;

    @RequestMapping("/register/{email}/{password}/{code}")
    public Integer register(HttpServletResponse response, @PathVariable(value = "email") String email, @PathVariable(value = "password") String password, @PathVariable(value = "code") String code) {
        int validateResult = codeFeignClient.validateCode(email, code);
        if (validateResult == 0) {
            String uuid = userService.register(email, password);
            if (!StringUtils.isEmpty(uuid)) {
                // uuid不为空证明登录成功，写入Cookie，返回email
                Cookie cookie = new Cookie("token", uuid);
                cookie.setPath("/");
                response.addCookie(cookie);
                return 0;
            } else {
                return -1;
            }
        } else {
            return validateResult;
        }
    }

    @RequestMapping("/isRegistered/{email}")
    public boolean isRegistred(@PathVariable(value = "email") String email) {
        return userService.isRegistered(email);
    }

    @RequestMapping("/login/{email}/{password}")
    public String login(HttpServletResponse response, @PathVariable(value = "email") String email, @PathVariable(value = "password") String password) {
        String uuid = userService.login(email, password);
        if (StringUtils.isEmpty(uuid)) {
            return "";
        }
        // uuid不为空证明登录成功，写入Cookie，返回email
        Cookie cookie = new Cookie("token", uuid);
        cookie.setPath("/");
        response.addCookie(cookie);
        return email;
    }

    @RequestMapping("/info/{token}")
    public String info(@PathVariable(value = "token") String token) {
        return userService.info(token);
    }

}
