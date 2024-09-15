package com.riemann.flink.state.limiter;

import org.apache.flink.streaming.connectors.kafka.internals.KafkaPartitionDiscoverer;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicsDescriptor;

import java.util.List;
import java.util.Properties;

public class DefaultKafkaPartitionDiscoverer<T> extends KafkaPartitionDiscoverer {

    private final DefaultDeserializationSchema<T> deserializer;

    public DefaultKafkaPartitionDiscoverer(KafkaTopicsDescriptor topicsDescriptor, int indexOfThisSubtask, int numParallelSubtasks, Properties kafkaProperties, DefaultDeserializationSchema<T> deserializer) {

        super(topicsDescriptor, indexOfThisSubtask, numParallelSubtasks, kafkaProperties);
        this.deserializer = deserializer;
    }

    @Override
    protected List<KafkaTopicPartition> getAllPartitionsForTopics(List<String> topics) throws WakeupException {

        List<KafkaTopicPartition> partitions = super.getAllPartitionsForTopics(topics);
        if (partitions != null) {
            this.deserializer.getLimiter().setTotalPartition(partitions.size());
        }
        return partitions;
    }

    @Override
    public List<KafkaTopicPartition> discoverPartitions() throws WakeupException, ClosedException {

        List<KafkaTopicPartition> subtaskPartitions = super.discoverPartitions();
        if (subtaskPartitions != null) {
            this.deserializer.getLimiter().setSubtaskPartition(subtaskPartitions.size());
        }
        return subtaskPartitions;
    }
}
