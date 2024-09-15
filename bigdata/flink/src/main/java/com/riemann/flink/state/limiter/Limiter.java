package com.riemann.flink.state.limiter;

import java.io.Serializable;

public interface Limiter extends Serializable {

    String getTopic();

    /**
     * 总分区数
     * @param totalPartition 总分区数
     */
    void setTotalPartition(int totalPartition);

    int getTotalPartition();

    /**
     * 当前子任务处理的分区数
     * @param subtaskPartition 当前子任务处理的分区数
     */
    void setSubtaskPartition(int subtaskPartition);

    int getSubtaskPartition();

    /**
     * 获取令牌
     * @return 获取到令牌的耗时毫秒数
     */
    double acquire();
}
