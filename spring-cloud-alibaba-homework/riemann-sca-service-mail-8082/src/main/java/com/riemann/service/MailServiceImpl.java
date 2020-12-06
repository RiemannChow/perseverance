package com.riemann.service;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.from}")
    private String sender;

    @Override
    public boolean sendTextMessage(String addressee, String subject, String messageContent) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(sender);
        simpleMailMessage.setTo(addressee);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(messageContent);
        javaMailSender.send(simpleMailMessage);
        return true;
    }

}
