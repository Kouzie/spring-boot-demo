package com.example.redis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DistributedLockService {

    private final RedissonClient redissonClient;
    private Integer count = 0;

    public void executeWithLock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        boolean isLocked = false;
        try {
            // 락을 획득하기 위해 100초 동안 시도하고, 10초 동안 락을 유지합니다.
            isLocked = lock.tryLock(100, 10, TimeUnit.SECONDS);
            if (isLocked) {
                // 락을 획득한 상태에서 실행할 작업
                Thread.sleep(100);
                count += 1;
                log.info("Lock acquired. Executing protected code. counter:{}", count);
                // 비즈니스 로직 실행
            } else {
                log.info("Could not acquire lock.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (isLocked) {
                lock.unlock();
                log.info("Lock released.");
            }
        }
    }

    public void executeWithoutLock() {
        try {
            Thread.sleep(100);
            count += 1;
            log.info("Lock acquired. Executing protected code. counter:{}", count);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}