package service.impl;

import com.riemann.service.IpPrintServiceC;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.rpc.RpcContext;

@Service
public class IpPrintServiceCImpl implements IpPrintServiceC {

    @Override
    public String printIp() {
        String clientIp = RpcContext.getContext().getAttachment("clientIp");
        System.out.println(clientIp);
        return clientIp;
    }

}
