package com.example.swagger.service;

import com.example.swagger.dto.TestRequest;
import com.example.swagger.dto.TestResponse;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    public TestResponse getResponse(TestRequest request) {
        return new TestResponse("hello", "world");
    }
}
