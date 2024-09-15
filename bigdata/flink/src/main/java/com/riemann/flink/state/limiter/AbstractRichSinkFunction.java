package com.riemann.flink.state.limiter;

import org.apache.flink.api.common.functions.AbstractRichFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public abstract class AbstractRichSinkFunction<T> extends AbstractRichFunction implements SinkFunction<T> {
}
