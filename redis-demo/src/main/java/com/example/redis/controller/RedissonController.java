package com.example.redis.controller;

import com.example.redis.service.DistributedLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/distribute-lock")
@RequiredArgsConstructor
public class RedissonController {

    private final DistributedLockService lockService;

    @GetMapping("/test")
    public String testLock() {
        lockService.executeWithLock("testLock");
        return "Lock test executed";
    }

    @GetMapping("/test/without/lock")
    public String testNonLock() {
        lockService.executeWithoutLock();
        return "Without Lock test executed";
    }
}
