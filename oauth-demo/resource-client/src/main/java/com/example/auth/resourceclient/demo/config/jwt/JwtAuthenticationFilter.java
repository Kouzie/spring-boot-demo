package com.example.auth.resourceclient.demo.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer";
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // check request header JWT
        final String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (authorization == null || !authorization.startsWith(BEARER)) {
            log.warn("JWT Token does not begin with Bearer String, url:{}", request.getRequestURL());
            request.setAttribute("exception", "INVALID AUTHORIZATION HEADER");
            chain.doFilter(request, response);
            return;
        }
        String token = authorization.substring(7);
        if (jwtUtil.isExpired(token)) {
            log.warn("JWT Token expired, url:{}", request.getRequestURL());
            request.setAttribute("exception", "EXPIRED TOKEN");
            chain.doFilter(request, response);
            return;
        }
        // generate auth object & save at security context
        Authentication authentication = getAuthentication(token); // generate auth object
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private Authentication getAuthentication(String token) {
        Collection<? extends GrantedAuthority> authorities = jwtUtil.getAuthorities(token);
        Map<String, Object> attributes = jwtUtil.getClaims(token);
        OAuth2User oAuth2User = new DefaultOAuth2User(authorities, attributes, "name");
        Authentication authToken = new UsernamePasswordAuthenticationToken(oAuth2User, null, oAuth2User.getAuthorities());
        return authToken;
    }
}

