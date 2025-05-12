package com.example.security.rest.config;

import com.example.security.common.config.CustomSecurityUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import java.util.List;
import java.util.Map;

@Slf4j
public class JwtSecurityContextRepository implements SecurityContextRepository {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer";
    private final List<String> ignoreUrls;

    public JwtSecurityContextRepository(List<String> ignoreUrls) {
        this.ignoreUrls = ignoreUrls;
    }

    // Spring Security 6.x 부터 loadDeferredContext 를 사용하며 실제 SecurityContext 를 호출하기 전까지 loadContext 는 deferred 됨,
    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // Ignore URLs check
        if (ignoreUrls.contains(request.getRequestURI())) {
            return context;
        }

        // Check JWT token
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (authorization == null || !authorization.startsWith(BEARER)) {
            log.warn("JWT Token does not begin with Bearer String, url:{}", request.getRequestURL());
            request.setAttribute("exception", "INVALID AUTHORIZATION HEADER");
            return context;
        }

        // Validate JWT and create Authentication
        String jwtToken = authorization.substring(7);
        Map<String, Object> claims = JwtTokenUtil.getAllClaimsFromToken(jwtToken);
        Authentication authentication = CustomSecurityUser.getAuthentication(claims);
        context.setAuthentication(authentication);

        return context;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        // Stateless이므로 SecurityContext를 저장하지 않음\
        log.info("saveContext invoked");
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        // Authorization 헤더가 있는 경우에만 SecurityContext 가 있다고 간주
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        return authorization != null && authorization.startsWith(BEARER);
    }
}