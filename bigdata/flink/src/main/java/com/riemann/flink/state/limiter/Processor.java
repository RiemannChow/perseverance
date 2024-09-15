package com.riemann.flink.state.limiter;

import java.io.Serializable;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public interface Processor<T> extends Serializable {

    void process(StreamExecutionEnvironment env);

    void setKafkaConfig(KafkaConfig kafkaConfig);

    String getTopic();

    void process(DataStream<T> dataStream);
}