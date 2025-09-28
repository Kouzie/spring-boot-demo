package com.example.observability.service.calculating.controller;

import com.example.observability.service.calculating.adaptor.GreetingServiceRestAdaptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/calculating")
public class CalculatingController {
    private final GreetingServiceRestAdaptor greetingServiceRestAdaptor;

    @GetMapping("/greet")
    public String greet() {
        return greetingServiceRestAdaptor.getGreeting();
    }

    @GetMapping("/{num1}/{num2}")
    public Long addNumbers(@PathVariable Long num1, @PathVariable Long num2) {
        log.info("addNumbers invoked, num1:{}, num2:{}", num1, num2);
        return num1 + num2;
    }

}
