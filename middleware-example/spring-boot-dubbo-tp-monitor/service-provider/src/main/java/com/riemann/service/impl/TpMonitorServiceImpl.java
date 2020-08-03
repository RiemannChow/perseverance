package com.riemann.service.impl;

import com.riemann.service.TpMonitorService;
import org.apache.dubbo.config.annotation.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

@Service
public class TpMonitorServiceImpl implements TpMonitorService {

    // volatile关键字修饰的调用次数字段
    private volatile int methodACount = 0;

    private Random random = new Random();

    // 开始时间
    private Instant startTime;

    // 标记变量，只打印一次
    private Boolean print = false;

    @Override
    public void methodA() {
        if (null == startTime) {
            startTime = Instant.now();
        }
        try {
            Thread.sleep(random.nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        methodACount++;

        // 统计一下半分钟请求的次数
        if (!print && Math.abs(Duration.between(Instant.now(), startTime).getSeconds()) > 30) {
            System.out.println("methodA called " + methodACount + " times");
            print = true;
        }
    }

    @Override
    public void methodB() {
        try {
            Thread.sleep(random.nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void methodC() {
        try {
            Thread.sleep(random.nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
