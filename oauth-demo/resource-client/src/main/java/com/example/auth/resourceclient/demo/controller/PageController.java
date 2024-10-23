package com.example.auth.resourceclient.demo.controller;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PageController {
    private final FilterChainProxy filterChainProxy;

    @PostConstruct
    public void printSecurityFilters() {
        List<SecurityFilterChain> filterChains = filterChainProxy.getFilterChains();
        for (SecurityFilterChain chain : filterChains) {
            List<Filter> filters = chain.getFilters();
            log.info("Security Filter Chain: " + chain);
//            OAuth2LoginAuthenticationFilter;
//            OAuth2AuthorizationRequestRedirectFilter
//            DefaultRedirectStrategy
//            ExceptionTranslationFilter
            for (Filter filter : filters) {
                log.info(filter.getClass().toString());
            }
        }
    }

    @GetMapping("/main")
    public String getMain() {
        return "main";
    }


    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
