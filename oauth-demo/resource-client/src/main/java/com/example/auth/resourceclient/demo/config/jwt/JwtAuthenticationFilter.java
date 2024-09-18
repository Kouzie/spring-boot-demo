package com.example.auth.resourceclient.demo.config.jwt;

import com.example.auth.resourceclient.demo.client.CustomOAuth2User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer";
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // check request header JWT
        Cookie[] cookies = request.getCookies() == null ? new Cookie[]{} : request.getCookies();
        String token = null;
        for (Cookie cookie : cookies) {
            System.out.println(cookie.getName());
            if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
                token = cookie.getValue();
            }
        }
        if (token == null) {
            log.warn("JWT Token does not begin with Bearer String, url:{}", request.getRequestURL());
            request.setAttribute("exception", "INVALID AUTHORIZATION HEADER");
            chain.doFilter(request, response);
            return;
        }
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
        Map<String, Object> attributes = jwtUtil.getClaims(token);
        // 회원가입 upsert 생략, 자체 서비스
        String nickname = attributes.get("nickname").toString();
        String email = attributes.get("email").toString();
        String name = attributes.get("name").toString();
        String registrationId = attributes.get("registrationId").toString();
        String role = "ROLE_USER";
        CustomOAuth2User oAuth2User =  new CustomOAuth2User(nickname,
                email,
                name,
                role,
                registrationId,
                null);
        Authentication authToken = new UsernamePasswordAuthenticationToken(oAuth2User, null, oAuth2User.getAuthorities());
        return authToken;
    }
}

