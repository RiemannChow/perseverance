package com.riemann.plugin;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;

import java.sql.Connection;
import java.util.Properties;

@Intercepts({
        @Signature(type = StatementHandler.class,
                   method = "prepare",
                   args = {Connection.class, Integer.class}
        )
})
public class MyPlugin implements Interceptor {

    // 拦截方法：只要被拦截的目标对象的目标方法被执行时，每次都会执行intercept方法
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("对方法进行了增强..."); // 这里可以去实现实现分页、监控等功能
        return invocation.proceed(); // 原方法执行
    }

    // 主要为了把当前的拦截器生成代理存到拦截器链中
    @Override
    public Object plugin(Object target) {
        Object wrap = Plugin.wrap(target, this);
        return wrap;
    }

    // 获取配置文件的参数
    @Override
    public void setProperties(Properties properties) {
        System.out.println("获取到的配置文件参数是：" + properties);
    }

}
