package com.example.observability.service.calculating.adaptor;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class GreetingServiceRestAdaptor {

    private final RestClient restClient;

    @Value("${service.greeting.url}")
    private String greetingServiceUrl;

    public String getGreeting() {
        return restClient.get()
                .uri(greetingServiceUrl + "/greeting")
                .retrieve()
                .body(String.class);
    }
}
