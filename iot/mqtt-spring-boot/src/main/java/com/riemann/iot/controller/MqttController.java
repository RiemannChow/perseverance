package com.riemann.iot.controller;

import com.riemann.iot.config.MqttGateWay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: 微信公众号【老周聊架构】
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class MqttController {
    @Autowired
    MqttGateWay mqttGateWay;

    @PostMapping("/publish")
    public String publish(@RequestHeader(value = "toplic") String toplic , String message) {
        log.info(String.format("topic: %s, message: %s", toplic, message));
        mqttGateWay.sendToMqtt(toplic, message);
        return "success";
    }
}
