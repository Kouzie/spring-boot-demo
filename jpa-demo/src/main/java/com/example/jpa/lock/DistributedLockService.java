package com.example.jpa.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DistributedLockService {

    private final NamedLockRepository lockRepository;
    private Integer count = 1;

    public void executeWithLock(String lockName) {
        int lockStatus = lockRepository.getLock(lockName, 10);
        if (lockStatus == 1) {
            try {
                // 락을 획득한 상태에서 실행할 작업
                Thread.sleep(100);
                count += 1;
                log.info("Lock acquired. Executing protected code. count:{}", count);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lockRepository.releaseLock(lockName);
                log.info("sLock released.");
            }
        } else {
            log.info("Could not acquire lock.");
        }
    }

    public void executeWithoutLock() {
        try {
            Thread.sleep(100);
            count += 1;
            log.info("Lock acquired. Executing protected code. count:{}", count);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}