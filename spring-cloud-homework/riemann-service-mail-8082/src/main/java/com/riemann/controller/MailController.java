package com.riemann.controller;

import com.riemann.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MailController {

    @Autowired
    MailService mailService;

    @RequestMapping("/email/{email}/{code}")
    public boolean email(@PathVariable(value = "email") String email, @PathVariable(value = "code") String code) {
        String subject = "注册邮箱验证码";
        String content = "您好，\n" +
                "您的验证码为:\n" +
                code + "\n" +
                "此验证码会在邮件发出10分钟后失效";

        return mailService.sendTextMessage(email, subject, content);
    }

}
