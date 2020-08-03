package com.riemann.service.pojo;

import lombok.Data;

import java.time.Instant;

@Data
public class MethodCallInfo {

    private Integer duration;

    private Instant lastCallTime;

}
