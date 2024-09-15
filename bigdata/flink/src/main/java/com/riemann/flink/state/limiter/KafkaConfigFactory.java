package com.riemann.flink.state.limiter;

import org.apache.flink.api.java.utils.ParameterTool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class KafkaConfigFactory {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConfigFactory.class);

    public static KafkaConfig create(Class<? extends KafkaConfig> clazz, ParameterTool parameter) {

        Method[] methods = clazz.getDeclaredMethods();
        KafkaConfig kafkaConfig = null;
        try {
            kafkaConfig = clazz.newInstance();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(PropertyValue.class)) {
                    continue;
                }
                PropertyValue propertyValue = method.getAnnotation(PropertyValue.class);
                if (!parameter.has(propertyValue.value())) {
                    continue;
                }
                Class<?> paramType = method.getParameterTypes()[0];
                Object value = null;
                if (paramType == String.class) {
                    value = parameter.get(propertyValue.value());
                } else if (paramType == boolean.class || paramType == Boolean.class) {
                    value = parameter.getBoolean(propertyValue.value(), false);
                } else if (paramType == int.class || paramType == Integer.class) {
                    value = parameter.getInt(propertyValue.value());
                } else if (paramType == long.class || paramType == Long.class) {
                    value = parameter.getLong(propertyValue.value());
                }
                method.invoke(kafkaConfig, value);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }
        return kafkaConfig;
    }
}