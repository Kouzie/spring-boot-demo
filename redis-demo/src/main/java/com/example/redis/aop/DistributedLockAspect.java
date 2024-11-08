package com.example.redis.aop;

import com.example.redis.annotation.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {
    private final RedissonClient redissonClient;

    @Around("@annotation(distributedLock) && args(lockKey,..)")
    public Object executeWithLock(ProceedingJoinPoint joinPoint,
                                  DistributedLock distributedLock,
                                  String lockKey) throws Throwable {
        String key = "lock:" + lockKey;
        RLock lock = redissonClient.getLock(key);
        log.info("Lock acquired. Executing protected code.");
        try {
            lock.lock();
            Object result = joinPoint.proceed();
            return result;
        } finally {
            lock.unlock();
            log.info("Lock released.");
        }
    }
}
