package com.example.security.rest.config;

import com.example.security.common.config.CustomSecurityUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // check request header JWT
        final String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (authorization == null || !authorization.startsWith(BEARER)) {
            log.warn("JWT Token does not begin with Bearer String, url:{}", request.getRequestURL());
            request.setAttribute("exception", "INVALID AUTHORIZATION HEADER"); //
        } else {
            // generate auth object & save at security context
            String jwtToken = authorization.substring(7);
            Map<String, Object> claims = JwtTokenUtil.getAllClaimsFromToken(jwtToken);
            Authentication authentication = CustomSecurityUser.getAuthentication(claims); // generate auth object
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}
