package com.riemann.zk;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
public class ZNode implements Serializable {

    private static final long serialVersionUID = 1L;

    private String url;

    private Integer port;

    private Instant lastResponseTime; // 最后一次返回的时间

    private long lastResponseDuration; // 响应时长

}
