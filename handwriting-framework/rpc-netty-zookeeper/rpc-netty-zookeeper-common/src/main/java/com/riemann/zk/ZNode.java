package com.riemann.zk;

import lombok.Data;

import java.io.Serializable;

@Data
public class ZNode implements Serializable {

    private String url;

    private Integer port;

}
