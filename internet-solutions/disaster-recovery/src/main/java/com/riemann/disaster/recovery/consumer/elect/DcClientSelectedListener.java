package com.riemann.disaster.recovery.consumer.elect;

import org.springframework.web.reactive.function.client.WebClient;

/**
 * 总部客户端选择监听器
 *
 * @author 微信公众号【老周聊架构】
 */
public interface DcClientSelectedListener {

    /**
     * 监听变更的客户端（看是生产客户端还是容灾客户端）
     */
    void selected(WebClient client);
}
