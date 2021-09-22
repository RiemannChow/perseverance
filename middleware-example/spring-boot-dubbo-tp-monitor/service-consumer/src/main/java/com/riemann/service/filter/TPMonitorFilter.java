package com.riemann.service.filter;

import com.riemann.service.pojo.MethodCallInfo;
import com.riemann.service.threads.MonitorThread;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.time.Instant;

@Activate(group = "originapi")
public class TPMonitorFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // 记录请求开始时间
        long start = System.currentTimeMillis();
        // 执行调用并获取返回值
        Result invoke = invoker.invoke(invocation);
        // 计算请求耗时
        int duration = (int) (System.currentTimeMillis() - start);
        // 封装请求信息对象，主要是封装当前请求时间，便于后续删除超过一分钟的数据
        MethodCallInfo methodCallInfo = new MethodCallInfo();
        methodCallInfo.setDuration(duration);
        methodCallInfo.setLastCallTime(Instant.now());
        // 记录本次请求数据
        MonitorThread.put(invocation.getMethodName(), methodCallInfo);
        // 返回调用结果
        return invoke;
    }

}
