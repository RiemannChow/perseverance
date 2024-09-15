package com.riemann.flink.state.limiter;

import java.util.List;

public interface KafkaConfig {

    List<String> topics();

    void setTopics(List<String> topics);

    String bootstrapServers();

    void setBootstrapServers(String bootstrapServers);

    String zookeeperConnect();

    void setZookeeperConnect(String zookeeperConnect);

    String groupId();

    void setGroupId(String groupId);

    String taskName();

    void setTaskName(String taskName);
}
