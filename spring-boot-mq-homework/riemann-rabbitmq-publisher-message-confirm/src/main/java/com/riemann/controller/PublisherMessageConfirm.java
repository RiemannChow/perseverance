package com.riemann.controller;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
public class PublisherMessageConfirm {
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setConfirmCallback((correlationData, flag, cause) -> {
            if (flag) {
                try {
                    System.out.println("消息确认:" + correlationData.getId() + " "
                            + new String(correlationData.getReturnedMessage().getBody(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(cause);
            }
        });
    }

    @RequestMapping("/biz")
    public String doBiz() throws UnsupportedEncodingException {
        MessageProperties props = new MessageProperties();
        props.setCorrelationId("1234");
        props.setConsumerTag("msg1");
        props.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN);
        props.setContentEncoding("utf-8");
        // props.setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT);
        // props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        CorrelationData cd = new CorrelationData();
        cd.setId("msg1");
        cd.setReturnedMessage(new Message("这是msg1的响应".getBytes("utf-8"), null));
        Message message = new Message("这是等待确认的消息".getBytes("utf-8"), props);
        rabbitTemplate.convertAndSend("ex.biz.publisher.message.confirm", "biz.publisher.message.confirm", message, cd);
        return "ok";
    }

    @RequestMapping("/bizFalse")
    public String doBizFalse() throws UnsupportedEncodingException {
        MessageProperties props = new MessageProperties();
        props.setCorrelationId("1234");
        props.setConsumerTag("msg1");
        props.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN);
        props.setContentEncoding("utf-8");
        Message message = new Message("这是等待确认的消息".getBytes("utf-8"), props);
        rabbitTemplate.convertAndSend("ex.bizFalse.publisher.message.confirm", "biz.publisher.message.confirm", message);
        return "ok";
    }
}