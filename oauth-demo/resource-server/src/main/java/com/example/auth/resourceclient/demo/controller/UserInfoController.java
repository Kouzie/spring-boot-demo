package com.example.auth.resourceclient.demo.controller;

import com.example.auth.resourceclient.demo.model.AuthUserDetailEntity;
import com.example.auth.resourceclient.demo.model.AuthUserDetailService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserInfoController {
    private final AuthUserDetailService service;
    private final FilterChainProxy filterChainProxy;

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
        JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> claims = authentication.getToken().getClaims();
        String uname = authentication.getName();
        OidcUserInfo.Builder oidcUserInfoBuilder = OidcUserInfo.builder()
                .claims(c -> c.putAll(claims));
        List<String> scopes = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        if (scopes.contains("SCOPE_profile")) {
            AuthUserDetailEntity entity = service.getUserById(uname);
            oidcUserInfoBuilder
                    .nickname(entity.getNickname())
                    .phoneNumber(entity.getPhone())
                    .birthdate(entity.getBirth())
                    .gender(entity.getGender());
        }
        return oidcUserInfoBuilder.build().getClaims();
    }
}
