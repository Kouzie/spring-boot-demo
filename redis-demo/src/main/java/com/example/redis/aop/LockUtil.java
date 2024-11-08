package com.example.redis.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class LockUtil {

    private final RedissonClient redissonClient;

    public <T> T withLock(String lockKey, Supplier<T> action) {
        log.info("Lock acquired. Executing protected code.");
        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.lock();
            return action.get(); // 람다식으로 전달된 코드 실행
        } finally {
            lock.unlock();
            log.info("Lock released.");
        }
    }
}