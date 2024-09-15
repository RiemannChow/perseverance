package com.riemann.flink.state.limiter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.RateLimiter;

public class DefaultRateLimiter implements Limiter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String topic;

    private int subtaskPartition;

    private int totalPartition;

    /**
     * tps
     */
    private int limit = 0;

    public DefaultRateLimiter() {
    }

    public DefaultRateLimiter(String topic, int limit) {

        this.topic = topic;
        this.limit = limit;
    }

    /**
     * 主题的限流器集合
     */
    private final Map<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    @Override
    public String getTopic() {

        return this.topic;
    }

    @Override
    public void setTotalPartition(int totalPartition) {

        this.totalPartition = totalPartition;
    }

    @Override
    public int getTotalPartition() {

        return this.totalPartition;
    }

    @Override
    public void setSubtaskPartition(int subtaskPartition) {

        this.subtaskPartition = subtaskPartition;
    }

    @Override
    public int getSubtaskPartition() {
        return this.subtaskPartition;
    }

    @Override
    public double acquire() {

        if (this.limit <= 0) {
            return 0;
        }
        if (this.totalPartition <= 0 || this.subtaskPartition <= 0) {
            return 0;
        }
        double spent = rateLimiterMap.computeIfAbsent(this.topic, k -> {
            double tps = new BigDecimal(this.limit).divide(new BigDecimal(totalPartition)).multiply(new BigDecimal(this.subtaskPartition))
                .setScale(2, RoundingMode.DOWN).doubleValue();
            return RateLimiter.create(tps);
        }).acquire();

        if (spent > 0) {
            logger.debug("限流器[{}]，触发限流，耗时{}s", this, spent);
        }
        return spent;
    }

    @Override
    public String toString() {

        return String.format("limiter: topic = %s, limit = %s/s, totalPartition = %s, subtaskPartition = %s.",
            this.topic, this.limit, this.totalPartition, this.subtaskPartition);
    }
}