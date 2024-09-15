package com.riemann.flink.state.limiter;

import java.util.Collections;
import java.util.List;

public class BizConfig implements KafkaConfig {

    @Override
    @PropertyValue("biz.topics")
    public List<String> topics() {
        return Collections.emptyList();
    }

    @Override
    public void setTopics(List<String> topics) {

    }

    @Override
    public String bootstrapServers() {
        return "";
    }

    @Override
    public void setBootstrapServers(String bootstrapServers) {

    }

    @Override
    public String zookeeperConnect() {
        return "";
    }

    @Override
    public void setZookeeperConnect(String zookeeperConnect) {

    }

    @Override
    public String groupId() {
        return "";
    }

    @Override
    public void setGroupId(String groupId) {

    }

    @Override
    public String taskName() {
        return "";
    }

    @Override
    public void setTaskName(String taskName) {

    }
}
