package com.riemann.flink.state.operatorstate;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.util.ArrayList;
import java.util.List;

public class CustomSink implements SinkFunction<Tuple2<String, Integer>>, CheckpointedFunction {

    // 用于缓存结果数据的
    private List<Tuple2<String, Integer>> bufferElements;
    // 表示内存中数据的大小阈值
    private int threshold;
    // 用于保存内存中的状态信息
    private ListState<Tuple2<String, Integer>> checkpointState;
    // StateBackend
    // checkpoint
    public CustomSink(int threshold) {
        this.threshold = threshold;
        this.bufferElements = new ArrayList<>();
    }

    // Sink的核心处理逻辑，将上游数据value输出到外部系统
    @Override
    public void invoke(Tuple2<String, Integer> value, Context context) throws Exception {
        // 可以将接收到的每一条数据保存到任何的存储系统中
        bufferElements.add(value);
        if (bufferElements.size() == threshold) {
            // send it to the sink
            // 这里简单打印
            System.out.println("自定义格式：" + bufferElements);
            // 清空本地缓存
            bufferElements.clear();
        }
    }

    // 重写CheckpointedFunction中的snapshotState
    // 将本地缓存snapshot保存到存储上
    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception
    {
        // 将之前的Checkpoint清理
        checkpointState.clear();
        // 将最新的数据写到状态中
        for (Tuple2<String, Integer> ele : bufferElements) {
            checkpointState.add(ele);
        }
    }

    // 重写CheckpointedFunction中的initializeState
    // 初始化状态：用于在程序恢复的时候从状态中恢复数据到内存
    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        // 注册ListStateDescriptor
        ListStateDescriptor<Tuple2<String, Integer>> descriptor =
            new ListStateDescriptor<>(
                "bufferd -elements",
                TypeInformation.of(new TypeHint<Tuple2<String, Integer>>() {}));
        // 从FunctionInitializationContext中获取OperatorStateStore，进而获取ListState
        checkpointState = context.getOperatorStateStore().getListState(descriptor);
        // 如果是作业重启，读取存储中的状态数据并填充到本地缓存中
        if (context.isRestored()) {
            for (Tuple2<String, Integer> ele : checkpointState.get()) {
                bufferElements.add(ele);
            }
        }
    }
}
