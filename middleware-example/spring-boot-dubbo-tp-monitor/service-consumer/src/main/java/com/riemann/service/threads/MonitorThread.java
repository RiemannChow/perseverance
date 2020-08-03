package com.riemann.service.threads;

import com.riemann.service.pojo.MethodCallInfo;
import com.riemann.service.utils.ApplicationContextUtil;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

@Component
public class MonitorThread implements Runnable {

    // 定义方法请求耗时的存储：key是方法名称，value是一个列表（存储对应方法1分钟内每次请求的耗时）
    private static ConcurrentHashMap<String, Vector<MethodCallInfo>> methodCalledTimeMap = new ConcurrentHashMap<>();

    // 计算TP90和TP99并打印
    @Override
    public void run() {
        Set<Map.Entry<String, Vector<MethodCallInfo>>> entries = methodCalledTimeMap.entrySet();
        for (Map.Entry<String, Vector<MethodCallInfo>> entry : entries) {
            List<MethodCallInfo> list = entry.getValue();
            // 数据小于100的时候不计算
            if (list.size() < 100) {
                continue;
            }
            // 先根据对象的请求时长进行排序
            list.sort(Comparator.comparingInt(MethodCallInfo::getDuration));
            // 然后计算出TP90和TP99的索引位置
            int index90 = (int) Math.floor(list.size() * 0.9);
            int index99 = (int) Math.floor(list.size() * 0.99);
            // 取出对应位置的耗时并打印
            Integer tp90 = list.get(index90).getDuration();
            Integer tp99 = list.get(index99).getDuration();
            System.out.println(entry.getKey() + "-size:" + list.size() + "-index:" + index90 + "-TP90:" + tp90);
            System.out.println(entry.getKey() + "-size:" + list.size() + "-index:" + index99 + "-TP99:" + tp99);
        }
    }

    // 开始打印
    public void startPrint() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // 每隔5秒打印
        MonitorThread bean = ApplicationContextUtil.getBean(MonitorThread.class);
        scheduledExecutorService.scheduleAtFixedRate(bean, 5, 5, TimeUnit.SECONDS);
    }

    // 添加一次调用数据
    public static void put(String methodName, MethodCallInfo methodCallInfo) {
        // 如果该方法的列表为空，则初始化为一个Vector
        if (methodCalledTimeMap.get(methodName) == null) {
            methodCalledTimeMap.put(methodName, new Vector<MethodCallInfo>());
        }
        methodCalledTimeMap.get(methodName).add(methodCallInfo);
    }

    // 删除1分钟之外的数据
    public void deleteOutTimeData() {
        Set<Map.Entry<String, Vector<MethodCallInfo>>> entries = methodCalledTimeMap.entrySet();
        for (Map.Entry<String, Vector<MethodCallInfo>> entry : entries) {
            Vector<MethodCallInfo> list = entry.getValue();
            // 要对list加锁，否则会有并发问题
            synchronized (list) {
                Iterator<MethodCallInfo> iterator = list.iterator();
                while (iterator.hasNext()) {
                    MethodCallInfo item = iterator.next();
                    // 如果该请求已经是1分钟之前的了，就移除掉
                    if (Math.abs(Duration.between(Instant.now(), item.getLastCallTime()).getSeconds()) > 60) {
                        iterator.remove();
                    }
                }
            }
        }
    }

}
