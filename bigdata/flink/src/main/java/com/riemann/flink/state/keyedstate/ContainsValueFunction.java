package com.riemann.flink.state.keyedstate;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.AggregatingState;
import org.apache.flink.api.common.state.AggregatingStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

public class ContainsValueFunction extends RichFlatMapFunction<Tuple2<Long, Long>, Tuple2<Long, String>> {
    private AggregatingState<Long, String> totalStr;

    @Override
    public void open(Configuration parameters) throws Exception {
        // 注册状态
        AggregatingStateDescriptor<Long, String, String> descriptor =
            new AggregatingStateDescriptor<>(
                "totalStr",  // 状态的名字
                new AggregateFunction<Long, String, String>() {
                    @Override
                    public String createAccumulator() {
                        return null;
                    }

                    @Override
                    public String add(Long value, String accumulator) {
                        if (StringUtils.isBlank(accumulator)) {
                            return String.valueOf(value);
                        }
                        return accumulator + " and " + value;
                    }

                    @Override
                    public String getResult(String accumulator) {
                        return accumulator;
                    }

                    @Override
                    public String merge(String a, String b) {
                        return null;
                    }
                }, String.class); // 状态存储的数据类型
        totalStr = getRuntimeContext().getAggregatingState(descriptor);
    }

    @Override
    public void flatMap(Tuple2<Long, Long> element,
                        Collector<Tuple2<Long, String>> out) throws Exception {
        totalStr.add(element.f1);
        out.collect(Tuple2.of(element.f0, totalStr.get()));
    }
}
