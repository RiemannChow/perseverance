package com.riemann.flink.state.limiter;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.internals.AbstractPartitionDiscoverer;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicsDescriptor;

import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultFlinkKafkaConsumer <T> extends FlinkKafkaConsumer<T> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultFlinkKafkaConsumer.class);

    private final DefaultDeserializationSchema<T> deserializer;

    public DefaultFlinkKafkaConsumer(List<String> topics, DefaultDeserializationSchema<T> deserializer, Properties properties) {

        super(topics, deserializer, properties);
        this.deserializer = deserializer;
    }

    @Override
    public void open(Configuration parameters) throws Exception {

        super.open(parameters);
        logger.info("限流器: {}", this.deserializer.getLimiter());
    }

    @Override
    protected AbstractPartitionDiscoverer createPartitionDiscoverer(KafkaTopicsDescriptor topicsDescriptor, int indexOfThisSubtask, int numParallelSubtasks) {

        return new DefaultKafkaPartitionDiscoverer<>(topicsDescriptor, indexOfThisSubtask, numParallelSubtasks, properties, deserializer);
    }
}
