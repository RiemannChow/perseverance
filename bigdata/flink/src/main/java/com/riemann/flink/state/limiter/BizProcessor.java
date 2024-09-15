package com.riemann.flink.state.limiter;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

import java.util.Objects;

@ProcessorRely(kafkaConfig = BizConfig.class)
public class BizProcessor extends AbstractStringProcessor {

    @Override
    public void process(DataStream<String> dataStream) {

        SingleOutputStreamOperator<BizDoc> ds = dataStream
                .map(new BizDocMapFunction())
                .name(operateName("originalMessageConvert"))
                .uid(uid("originalMessageConvert"))
                .filter(Objects::nonNull)
                .name(operateName("messageNullFilter"))
                .uid(uid("messageNullFilter"));

        collectWindowAll(ds)
                .name(operateName("collectMessage"))
                .uid(uid("collectMessage"))
                .addSink(new BizSink())
                .name(operateName("messageSink"))
                .uid(uid("messageSink"));
    }

    protected String operateName(String name) {

        return String.format("%s-%s", getKafkaConfig().taskName(), name);
    }

    protected String uid(String simpleOperatorName) {

        return String.format("%s-%s-uid-%s", getKafkaConfig().taskName(), simpleOperatorName, getTopic());
    }
}