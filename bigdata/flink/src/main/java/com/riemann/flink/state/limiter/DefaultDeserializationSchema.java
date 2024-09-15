package com.riemann.flink.state.limiter;

import org.apache.flink.api.common.serialization.DeserializationSchema;

public interface DefaultDeserializationSchema<T> extends DeserializationSchema<T> {

    Limiter getLimiter();
}
