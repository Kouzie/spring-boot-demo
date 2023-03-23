package com.example.quartz.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestService {
    @Value("${test.string}")
    private String testString;

    public void test() {
        log.info(testString);
    }
}
