package com.example.statemachine.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.data.redis.*;
import org.springframework.statemachine.persist.RepositoryStateMachinePersist;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

import static com.example.statemachine.demo.model.OrderEntity.OrderEvents;
import static com.example.statemachine.demo.model.OrderEntity.OrderStates;

@Slf4j
@Configuration
public class OrderStateMachinePersistConfig {
    @Value("${redis.host}")
    private String host;
    @Value("${redis.port}")
    private int port;

    @Bean
    RedisConnectionFactory redisConnectionFactory() {
        RedisConnectionFactory connectionFactory = new LettuceConnectionFactory(host, port);
        return connectionFactory;
    }

    @Bean
    public StateMachinePersist<OrderStates, OrderEvents, String> stateMachinePersist(RedisConnectionFactory connectionFactory) {
        RedisStateMachineContextRepository<OrderStates, OrderEvents> repository = new RedisStateMachineContextRepository<>(connectionFactory);
        return new RepositoryStateMachinePersist<>(repository);
    }

    @Bean
    public StateMachinePersister<OrderStates, OrderEvents, String> redisStateMachinePersister(
            StateMachinePersist<OrderStates, OrderEvents, String> stateMachinePersist) {
        return new RedisStateMachinePersister<>(stateMachinePersist);
    }

    @Bean
    public StateMachineService<OrderStates, OrderEvents> stateMachineService(
            StateMachineFactory<OrderStates, OrderEvents> stateMachineFactory,
            StateMachinePersist<OrderStates, OrderEvents, String> stateMachinePersist) {
        return new DefaultStateMachineService<>(stateMachineFactory, stateMachinePersist);
    }

    @Bean
    public StateMachineRuntimePersister<OrderStates, OrderEvents, String> stateMachineRuntimePersister(
            RedisStateMachineRepository stateMachineRepository) {
        // SimpleKeyValueRepository
        return new RedisPersistingStateMachineInterceptor<>(stateMachineRepository);
    }

}