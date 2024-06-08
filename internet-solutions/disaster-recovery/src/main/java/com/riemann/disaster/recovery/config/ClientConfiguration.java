package com.riemann.disaster.recovery.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

import com.mongodb.ReadPreference;
import com.riemann.disaster.recovery.consumer.elect.DcClientSelector;
import com.riemann.disaster.recovery.consumer.elect.HaDcClientSelector;

@EnableAsync
@Configuration
@EnableConfigurationProperties(InoutProperties.class)
public class ClientConfiguration {

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(1);
        taskExecutor.setMaxPoolSize(50);
        taskExecutor.setQueueCapacity(10);
        return taskExecutor;
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory, MongoConverter converter) {

        ReactiveMongoTemplate template = new ReactiveMongoTemplate(
            reactiveMongoDatabaseFactory, converter);
        template.setReadPreference(ReadPreference.primaryPreferred());
        return template;
    }

    @Bean
    public DcClientSelector dcClientSelector(InoutProperties properties) {

        return new HaDcClientSelector(properties);
    }
}
