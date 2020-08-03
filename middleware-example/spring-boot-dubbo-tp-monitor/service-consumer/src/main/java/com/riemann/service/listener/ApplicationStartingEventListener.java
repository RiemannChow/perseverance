package com.riemann.service.listener;

import com.riemann.service.threads.CallProvider;
import com.riemann.service.threads.DeleteOutTimeItemsThread;
import com.riemann.service.threads.MonitorThread;
import com.riemann.service.utils.ApplicationContextUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ApplicationStartingEventListener implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("application started...");
        MonitorThread monitorThread = ApplicationContextUtil.getBean(MonitorThread.class);
        monitorThread.startPrint();

        DeleteOutTimeItemsThread deleteOutTimeItemsThread = ApplicationContextUtil.getBean(DeleteOutTimeItemsThread.class);
        deleteOutTimeItemsThread.startDelete();

        callMethod();
    }

    public static void callMethod() {
        CallProvider bean = ApplicationContextUtil.getBean(CallProvider.class);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    bean.callMethod();
                }
            });
            try {
                Thread.sleep(27);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
