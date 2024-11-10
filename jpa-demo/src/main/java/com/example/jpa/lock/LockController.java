package com.example.jpa.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/distribute-lock")
public class LockController {

    private final DistributedLockService lockService;

    @GetMapping("/test")
    public String testLock() {
        lockService.executeWithLock("testLock");
        return "Lock test executed";
    }

    @GetMapping("/test/without/lock")
    public String testWithoutLock() {
        lockService.executeWithoutLock();
        return "Without Lock test executed";
    }
}