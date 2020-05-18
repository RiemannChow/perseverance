package com.riemann.rpc.dto;

import java.io.Serializable;

public class Invocation implements Serializable {

    // 接口名
    private String className;

    // 方法名
    private String methodName;

    // 方法参数类型列表
    private Class<?>[] paramTypes;

    // 方法参数值列表
    private Object[] paramValues;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getParamValues() {
        return paramValues;
    }

    public void setParamValues(Object[] paramValues) {
        this.paramValues = paramValues;
    }
}
