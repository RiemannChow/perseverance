package com.riemann.disaster.recovery.consumer.elect;

import org.springframework.web.reactive.function.client.WebClient;

import com.riemann.disaster.recovery.config.InoutProperties;

/**
 * 总部客户端选择器
 *
 * @author 微信公众号【老周聊架构】
 */
public interface DcClientSelector {

    /**
     * 获取总部客户端
     */
    WebClient getDcClient();

    /**
     * 刷新配置
     */
    void refresh(InoutProperties properties);

    /**
     * 选择任务开始
     */
    void start();

    /**
     * 选择任务停止
     */
    void stop();
}
