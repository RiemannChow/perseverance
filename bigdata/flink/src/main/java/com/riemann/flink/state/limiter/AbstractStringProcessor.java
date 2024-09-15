package com.riemann.flink.state.limiter;

import java.util.List;

public abstract class AbstractStringProcessor extends AbstractProcessor<String> {

    public AbstractStringProcessor() {

    }

    @Override
    protected DefaultDeserializationSchema<String> deserializationSchema(List<String> topics, int limit) {

        return new DefaultSimpleStringSchema(topics, limit);
    }
}