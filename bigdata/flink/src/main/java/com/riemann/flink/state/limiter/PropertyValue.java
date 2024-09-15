package com.riemann.flink.state.limiter;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Inherited
public @interface PropertyValue {

    String value();
}