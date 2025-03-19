package com.example.config.spel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogTestService {

    @LogPrint(prefix = "'User ID: ' + #userId + ', Action: ' + #action")
    public void execute(String userId, String action) {
        log.info("비즈니스 로직 실행 - userId: {}, action: {}", userId, action);
    }
}