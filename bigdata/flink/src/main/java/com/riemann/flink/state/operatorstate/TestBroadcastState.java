package com.riemann.flink.state.operatorstate;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;

/**
 * 数据流：
 * i love flink
 * 广播流：
 * key  flink  -> 代表数据流里面，只要包含flink的单词才会被打印出来。
 */
public class TestBroadcastState {
    public static void main(String[] args) throws Exception {
        //获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 1. 定义普通数据流，消费数据
        DataStreamSource<String> dataStreamSource = env.socketTextStream("localhost", 9999);
        // 2. 定义广播流，用于广播规则，从而控制程序打印输出
        DataStreamSource<String> broadStreamSource = env.socketTextStream("localhost", 8888);
        // 3. 解析广播流中的数据成二元组
        DataStream<Tuple2<String, String>> broadStream =
            broadStreamSource.map((MapFunction<String, Tuple2<String, String>>) s -> {
                String[] strings = s.split(" ");
                return Tuple2.of(strings[0], (strings[1]));
            });
        //4. 定义需要广播的状态类型，只支持MapState
        MapStateDescriptor<String, String> descriptor = new
            MapStateDescriptor<>(
            "ControlStream",
            String.class,
            String.class
        );
        //5. 用解析后的广播流将状态广播出去，从而生成BroadcastStream
        BroadcastStream<Tuple2<String, String>> broadcastStream = broadStream.broadcast(descriptor);
        //6. 通过connect连接两个流，用process分别处理两个流中的数据
        dataStreamSource
            .connect(broadcastStream)
            .process(new KeyWordsCheckProcessor())
            .print();
        env.execute();
    }

    private static class KeyWordsCheckProcessor extends BroadcastProcessFunction<String, Tuple2<String, String>, String> {
        MapStateDescriptor<String, String> descriptor =
            new MapStateDescriptor<>(
                "ControlStream",
                String.class,
                String.class
            );
        @Override
        public void processBroadcastElement(Tuple2<String, String> value, Context ctx, Collector<String> out) throws Exception {
            // 将接收到的控制数据放到 broadcast state 中
            ctx.getBroadcastState(descriptor).put(value.f0, value.f1);
            // 打印控制信息
            System.out.println(Thread.currentThread().getName() + " 接收到控制信息 ：" + value);
        }

        @Override
        public void processElement(String value, ReadOnlyContext ctx, Collector<String> out) throws Exception {
            // 从 broadcast state 中拿到控制信息
            String keywords = ctx.getBroadcastState(descriptor).get("key");
            // 获取符合条件的单词
            if (value.contains(keywords)) {
                out.collect(value);
            }
        }
    }
}
