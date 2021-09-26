package com.riemann.filter;

import com.riemann.utils.ThreadLocalUtil;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

@Activate(group = "originapi")
public class TransportIPFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // 从ThreadLocal中取出IP
        String remoteAddr = ThreadLocalUtil.get();
        // 放入RpcContext中
        RpcContext.getContext().setAttachment("clientIp", remoteAddr);
        return invoker.invoke(invocation);
    }

}
