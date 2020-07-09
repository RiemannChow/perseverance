package com.riemann.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    private Integer id; //id

    private String username; //用户名

    private String password; //密码

}