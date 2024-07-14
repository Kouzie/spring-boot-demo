package com.example.eventlistener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // 기본적으로 유지할 스레드 수
        executor.setMaxPoolSize(50); // 최대 스레드 수
        executor.setQueueCapacity(100); // 큐 용량
        executor.setThreadNamePrefix("AsyncThread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 처리되지 않은 작업 처리 정책
        executor.initialize();
        return executor;
    }
}