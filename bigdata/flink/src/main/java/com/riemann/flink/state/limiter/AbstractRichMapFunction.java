package com.riemann.flink.state.limiter;

import org.apache.flink.api.common.functions.AbstractRichFunction;
import org.apache.flink.api.common.functions.MapFunction;

public abstract class AbstractRichMapFunction<T, R>  extends AbstractRichFunction implements MapFunction<T, R> {
}
