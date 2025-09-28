package com.example.observability.service.greeting.adaptor;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class CalculatingServiceRestAdaptor {

    private final RestClient restClient;

    @Value("${service.calculating.url}")
    private String calculatingServiceUrl;

    public Long addNumbers(Long num1, Long num2) {
        return restClient.get()
                .uri(calculatingServiceUrl + "/calculating/{num1}/{num2}", num1, num2)
                .retrieve()
                .body(Long.class);
    }
}
