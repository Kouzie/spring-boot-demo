package com.example.websock;

import org.springframework.stereotype.Component;

@Component
public class TestComponent {
    private String testString = "testString";

    public Long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
