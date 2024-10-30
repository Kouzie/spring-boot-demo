package com.example.auth.resourceserver.demo.controller;

import com.example.auth.resourceserver.demo.model.AuthUserDetailEntity;
import com.example.auth.resourceserver.demo.model.AuthUserDetailService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserInfoController {
    private final AuthUserDetailService service;
    private final FilterChainProxy filterChainProxy;
    private final Environment env;

    @PostConstruct
    public void printSecurityFilters() {
        List<SecurityFilterChain> filterChains = filterChainProxy.getFilterChains();
        for (SecurityFilterChain chain : filterChains) {
            List<Filter> filters = chain.getFilters();
            log.info("Security Filter Chain: " + chain);
            for (Filter filter : filters) {
                log.info(filter.getClass().toString());
            }
        }
    }
    /*
    Security Filter Chain: DefaultSecurityFilterChain
    class org.springframework.security.web.session.DisableEncodeUrlFilter
    class org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter
    class org.springframework.security.web.context.SecurityContextHolderFilter
    class org.springframework.security.web.header.HeaderWriterFilter
    class org.springframework.web.filter.CorsFilter
    class org.springframework.security.web.csrf.CsrfFilter
    class org.springframework.security.web.authentication.logout.LogoutFilter
    class org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter
    class org.springframework.security.web.savedrequest.RequestCacheAwareFilter
    class org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter
    class org.springframework.security.web.authentication.AnonymousAuthenticationFilter
    class org.springframework.security.web.session.SessionManagementFilter
    class org.springframework.security.web.access.ExceptionTranslationFilter
    class org.springframework.security.web.access.intercept.AuthorizationFilter
    */

    @GetMapping("/userinfo")
    public Map<String, Object> getUserinfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uname = authentication.getName();
        AbstractOAuth2TokenAuthenticationToken<?> oAuth2TokenAuthenticationToken = (AbstractOAuth2TokenAuthenticationToken<?>) authentication;
        Map<String, Object> response = new HashMap<>();
        if (oAuth2TokenAuthenticationToken.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("SCOPE_profile"))) {
            AuthUserDetailEntity entity = service.getUserById(uname);
            response.putAll(oAuth2TokenAuthenticationToken.getTokenAttributes());
            response.put("nickname", entity.getNickname());
            response.put("phone_number", entity.getPhone());
            response.put("birthdate", entity.getBirth());
            response.put("gender", entity.getGender());
        }
        return response;
    }
}
