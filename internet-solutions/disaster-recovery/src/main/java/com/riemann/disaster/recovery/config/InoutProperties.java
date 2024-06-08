package com.riemann.disaster.recovery.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

public class InoutProperties {

    private RemoteConfig remote = new RemoteConfig();

    public RemoteConfig getRemote() {

        return remote;
    }

    public void setRemote(RemoteConfig remote) {

        this.remote = remote;
    }

    @EqualsAndHashCode
    @ToString
    @Data
    public static class RemoteConfig {

        private String baseZno;

        private String consumerId;

        /**
         * 请求超时
         */
        private Duration readTimeout;

        private Integer fetchCount;

        private Duration fetchInterval;

        private Duration minWaitTime;

        /**
         * 拉取数据最大延迟，只拉取MaxLatency内的数据
         */
        private Duration prMaxLatency;

        /**
         * 从多久之前开始消费，相当于先重置偏移量。一次性配置，配置生效后请删除
         */
        private Duration prFetchBefore;

        /**
         * 总部网关组VIP组成的地址，包括协议和端口，多个用“，”分割
         * http://xxx.xxx.xxx.xxx:18080,http://yyy.yyy.yyy.yyy:18080
         */
        private String dcIpHosts;

        /**
         * 总部网关域名，作为http请求头“host”，不带协议和端口
         */
        private String dcDomainHost;

        /**
         * 总部健康检查地址
         */
        private String dispGwHealthUri = "/riemann/health-check";

        private String dcApiHost;


        public String getConsumerId() throws UnknownHostException {

            return consumerId == null ? InetAddress.getLocalHost().getHostName() : consumerId;
        }

        public void setConsumerId(String consumerId) {

            this.consumerId = consumerId;
        }
    }

}
