package com.riemann.flink.state.limiter;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ListTypeInfo;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.List;
import java.util.Properties;

import com.google.common.collect.Lists;

public abstract class AbstractProcessor<T> implements Processor<T> {

    private int parallelism;
    private int maxParallelism;
    private int kafkaParallelism;
    protected KafkaConfig kafkaConfig;

    @Override
    public void process(StreamExecutionEnvironment env) {

        ParameterTool parameter = (ParameterTool) env.getConfig().getGlobalJobParameters();
        KafkaConfig kafkaConfig = KafkaConfigFactory.create(this.getClass().getAnnotation(ProcessorRely.class).kafkaConfig(), parameter);
        this.parallelism = env.getParallelism();
        this.maxParallelism = env.getMaxParallelism();

        int limit = parameter.getInt("TASK_QPS", 0);
        Properties properties = new Properties();
        properties.setProperty("KAFKA_PROPERTY_KEY_BOOTSTRAP_SERVERS", kafkaConfig.bootstrapServers());
        properties.setProperty("KAFKA_PROPERTY_KEY_ZOOKEEPER_CONNECT", kafkaConfig.zookeeperConnect());
        properties.setProperty("KAFKA_PROPERTY_KEY_GROUP_ID", kafkaConfig.groupId());
        FlinkKafkaConsumer<T> consumer = new DefaultFlinkKafkaConsumer<>(kafkaConfig.topics(), deserializationSchema(kafkaConfig.topics(), limit), properties);
        DataStream<T> source = env.addSource(consumer).setParallelism(this.kafkaParallelism).name(getTopic()).uid(String.format("source-uid-%s", getTopic()));
        process(source);
    }

    protected abstract DefaultDeserializationSchema<T> deserializationSchema(List<String> topics, int limit);

    public int getKafkaParallelism() {
        return kafkaParallelism;
    }

    public void setKafkaParallelism(int kafkaParallelism) {
        this.kafkaParallelism = kafkaParallelism;
    }

    public KafkaConfig getKafkaConfig() {
        return kafkaConfig;
    }

    @Override
    public void setKafkaConfig(KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
    }

    @Override
    public String getTopic() {
        return StringUtils.join(kafkaConfig.topics().toArray());
    }

    protected <E> SingleOutputStreamOperator<List<E>> collectWindowAll(DataStream<E> dataStream) {

        TypeInformation<List<E>> type = getType(dataStream.getType().getTypeClass());
        return dataStream.windowAll(TumblingProcessingTimeWindows.of(Time.milliseconds(1000L)))
            .trigger(null)
            .apply((window, input, out) -> out.collect(Lists.newArrayList(input)), type);
    }

    private <E> TypeInformation<List<E>> getType(Class<E> clazz) {

        return new ListTypeInfo<>(clazz);
    }
}