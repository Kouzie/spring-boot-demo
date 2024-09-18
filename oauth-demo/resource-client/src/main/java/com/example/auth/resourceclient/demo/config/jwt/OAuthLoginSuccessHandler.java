package com.example.auth.resourceclient.demo.config.jwt;

import com.example.auth.resourceclient.demo.client.CustomOAuth2User;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Profile("jwt")
@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String token = jwtUtil.createJwt(oAuth2User, 60 * 60 * 60L);
        response.addCookie(createCookie("Authorization", token));
        response.sendRedirect("http://127.0.0.1:8080/main");
    }

    private Cookie createCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // HTTPS가 필요한 경우 true로 설정
        cookie.setPath("/");
        //cookie.setDomain(""); localhost 환경에서 사용 X
        cookie.setMaxAge(60 * 60 * 60);
        return cookie;
    }
}