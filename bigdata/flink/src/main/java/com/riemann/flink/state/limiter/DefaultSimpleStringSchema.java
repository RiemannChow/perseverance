package com.riemann.flink.state.limiter;

import org.apache.flink.api.common.serialization.SimpleStringSchema;

import java.util.List;

public class DefaultSimpleStringSchema extends SimpleStringSchema implements DefaultDeserializationSchema<String>{

    private final Limiter limiter;

    public DefaultSimpleStringSchema(List<String> topics, int limit) {

        super();
        this.limiter = new DefaultRateLimiter(topics.get(0), limit);
    }

    @Override
    public String deserialize(byte[] message) {

        this.limiter.acquire();
        return super.deserialize(message);
    }

    @Override
    public Limiter getLimiter() {

        return this.limiter;
    }
}