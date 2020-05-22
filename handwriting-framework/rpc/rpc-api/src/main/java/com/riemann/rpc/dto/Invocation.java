package com.riemann.rpc.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class Invocation implements Serializable {

    // 接口名
    private String className;

    // 方法名
    private String methodName;

    // 方法参数类型列表
    private Class<?>[] paramTypes;

    // 方法参数值列表
    private Object[] paramValues;

}
