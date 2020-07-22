package com.riemann.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DataBaseConfig implements Serializable {

    private final static long serialVersionUID = 1L;

    private String driver;

    private String url;

    private String username;

    private String password;

}
