package com.riemann.service.threads;

import com.riemann.service.utils.ApplicationContextUtil;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class DeleteOutTimeItemsThread implements Runnable {

    @Override
    public void run() {
        MonitorThread bean = ApplicationContextUtil.getBean(MonitorThread.class);
        bean.deleteOutTimeData();
    }

    // 开始删除
    public void startDelete() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // 每隔5秒删除一次
        DeleteOutTimeItemsThread bean = ApplicationContextUtil.getBean(DeleteOutTimeItemsThread.class);
        scheduledExecutorService.scheduleAtFixedRate(bean, 7, 5, TimeUnit.SECONDS);
    }

}
