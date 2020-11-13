package com.riemann.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实现Ip防爆刷
 */
@Slf4j
@Component
public class IpControlFilter implements GlobalFilter, Ordered {

    public static List<String> limitedIpMap = Collections.synchronizedList(new ArrayList<>());
    public static Map<String, List<Long>> ipVisitedMap = new ConcurrentHashMap<>();

    /**
     * 默认限制时间（单位：ms）
     */
    private static long LIMITED_TIME_MILLIS;

    /**
     * 用户连续访问最高阀值，超过该值则认定为恶意操作的IP，进行限制
     */
    @Value("${control.limit.number}")
    private static int LIMIT_NUMBER;

    @Value("${control.limit.time}")
    public void setLimitedTime(long time) {
        IpControlFilter.LIMITED_TIME_MILLIS = time;
    }

    @Value("${control.limit.number}")
    public void setLimitNumber(int number) {
        IpControlFilter.LIMIT_NUMBER = number;
    }

    /**
     * 开始清除线程的标志
     */
    private static boolean START_CLEAN_FLAG = false;

    /**
     * 过滤器核心方法
     * @param exchange 封装了request和response对象的上下文
     * @param chain 网关过滤器链（包含全局过滤器和单路由过滤器）
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 思路：获取客户端ip，判断是否在黑名单中，在的话就拒绝访问，不在的话就放行
        // 从上下文中取出request和response对象
        ServerHttpRequest request = exchange.getRequest();
        RequestPath path = request.getPath();

        if (!path.toString().startsWith("/api/user/register/")) {
            // 不是注册请求的，直接不过滤
            return chain.filter(exchange);
        }
        ServerHttpResponse response = exchange.getResponse();
        String ip = request.getRemoteAddress().getHostString();
        // 判断是否是被限制的IP，如果是则跳到异常页面
        if (isLimitedIP(ip)) {
            System.out.println("IP访问过于频繁，限制访问" + ip);
            if (!START_CLEAN_FLAG) {
                START_CLEAN_FLAG = true;
                System.out.println("开始清除");
                startCleanLimitedIp();
            }
            response.setStatusCode(HttpStatus.OK); // 状态码
            String data = "您的IP访问过于频繁，请稍后再试";
            DataBuffer wrap = response.bufferFactory().wrap(data.getBytes());
            response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
            return response.writeWith(Mono.just(wrap));
        }

        // 获取IP存储器
        // 判断存储器中是否存在当前IP，如果没有则为初次访问，初始化该ip
        // 如果存在当前ip，则验证当前ip的访问次数
        // 如果大于限制阀值，判断达到阀值的时间，如果不大于[用户访问最小安全时间]则视为恶意访问，跳转到异常页面
        if (!checkCanVisit(ip)) {
            response.setStatusCode(HttpStatus.OK); // 状态码
            String data = "您的IP访问过于频繁，请稍后再试";
            DataBuffer wrap = response.bufferFactory().wrap(data.getBytes());
            response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
            return response.writeWith(Mono.just(wrap));
        } else {
            if (null == ipVisitedMap.get(ip)) {
                initIpVisitsNumber(ip);
                System.out.println("首次访问该网站");
            } else {
                addVistedRecord(ip);
                System.out.println("正常访问");
            }
        }

        // 合法请求，放行，执行后续的过滤器
        return chain.filter(exchange);
    }

    /**
     * 返回值表示当前过滤器的顺序(优先级)，数值越小，优先级越高，此处返回10，预留添加其他更高优先级过滤器的空间
     * @return
     */
    @Override
    public int getOrder() {
        return 10;
    }

    /**
     * 增加访问记录
     * @param ip
     */
    private void addVistedRecord(String ip) {
        List<Long> ipVisitedRecords = ipVisitedMap.get(ip);
        ipVisitedRecords.add(System.currentTimeMillis());
        ipVisitedMap.put(ip, ipVisitedRecords);
    }

    /**
     * 初始化用户访问次数和访问时间
     * @param ip
     */
    private void initIpVisitsNumber(String ip) {
        List<Long> list = Collections.synchronizedList(new ArrayList<>());
        list.add(System.currentTimeMillis());
        ipVisitedMap.put(ip, list);
    }

    /**
     * 检查是否被限制
     * @param ip
     */
    private boolean checkCanVisit(String ip) {
        if (null == ipVisitedMap.get(ip)) {
            return true;
        }

        List<Long> vistedTimes = ipVisitedMap.get(ip);
        if (vistedTimes.size() >= LIMIT_NUMBER) {
            limitedIpMap.add(ip);
            return false;
        }

        return true;
    }

    /**
     * 开始清除。定义一个线程
     */
    private static void startCleanLimitedIp() {
        int period = (int) LIMITED_TIME_MILLIS;
        new Timer("clean limited ip timer").schedule(new TimerTask() {
            @Override
            public void run() {
                doCleanLimitedIp();
            }
        }, period, period - 1);
    }

    /**
     * 过滤受限的IP，剔除已经到期的限制IP
     */
    private static void doCleanLimitedIp() {
        if (limitedIpMap.size() < 1) {
            return;
        }
        Iterator<Map.Entry<String, List<Long>>> iterator = ipVisitedMap.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, List<Long>> next = iterator.next();
            List<Long> ipVistedRecord = next.getValue();
            Iterator<Long> ipIterator = ipVistedRecord.iterator();
            while(ipIterator.hasNext()) {
                Long visitTime = ipIterator.next();
                Long duration = (System.currentTimeMillis() - visitTime) / 1000;
                if (duration > LIMITED_TIME_MILLIS) {
                    // 清除访问记录
                    System.out.println("清除访问记录" + next.getKey() + "-" + visitTime);
                    ipIterator.remove();
                }
            }
            if (ipVistedRecord.size() < LIMIT_NUMBER) {
                limitedIpMap.remove(next.getKey());
            }
        }
    }

    /**
     * 是否是被限制的IP
     * @param ip
     * @return true : 被限制 | false : 正常
     */
    private boolean isLimitedIP(String ip) {
        if (limitedIpMap.contains(ip)) {
            return true;
        }
        return false;
    }

}
