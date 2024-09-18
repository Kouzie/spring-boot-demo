package com.example.auth.server.demo.client.controller;

import com.example.auth.server.demo.client.service.ClientRegistrationService;
import com.example.auth.server.demo.client.dto.ClientRegistrationRequest;
import com.example.auth.server.demo.client.dto.ClientRegistrationResponse;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class ClientRegistrationController {
    private final ClientRegistrationService clientRegistrationService;
    private final FilterChainProxy filterChainProxy;

    @PostConstruct
    public void printSecurityFilters() {
        List<SecurityFilterChain> filterChains = filterChainProxy.getFilterChains();
        for (SecurityFilterChain chain : filterChains) {
            var filters = chain.getFilters();
            log.info("Security Filter Chain: " + chain);
            for (var filter : filters) {
                log.info(filter.getClass().toString());
            }
        }
    }
    @PostMapping
    public ResponseEntity<ClientRegistrationResponse> registerClient(@RequestBody ClientRegistrationRequest request) {
        // 클라이언트 등록 로직 (DB에 저장하거나 동적 생성)
        ClientRegistrationResponse response = clientRegistrationService.register(request);
        return ResponseEntity.ok(response);
    }
}
