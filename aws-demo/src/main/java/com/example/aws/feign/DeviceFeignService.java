package com.example.aws.feign;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping("/device")
public interface DeviceFeignService {

    @GetMapping("/test")
    Map<String, Object> getCustomerInformation();
}