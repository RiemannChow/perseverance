package com.riemann.utils;

public class ThreadLocalUtil {

    // 定义静态变量放置ThreadLocal
    static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    // 提供写入方法
    public static void put(String str) {
        threadLocal.set(str);
    }
    // 提供读取方法
    public static String get() {
        return threadLocal.get();
    }

}
