package com.example.auth.resourceclient.demo.config.jwt;

import com.example.auth.resourceclient.demo.client.CustomOAuth2User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

@Slf4j
@Profile("jwt")
@Component
public class JwtUtil {

    private SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            log.debug("expired jwt excetpion, {}", e.getMessage());
            return true;
        } catch (Exception e) {
            log.debug("unknown jwt excetpion, {}", e.getMessage());
            return true;
        }
    }

    public Map<String, Object> getClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    public Collection<? extends GrantedAuthority> getAuthorities(String token) {
        String authorities = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("authorities", String.class);
        return Arrays.stream(authorities.split(",")).map(authority -> new SimpleGrantedAuthority(authorities)).toList();
    }


    public String createJwt(CustomOAuth2User oAuth2User, long expiredMs) {
        return Jwts.builder()
                .claim("nickname", oAuth2User.getNickname())
                .claim("email", oAuth2User.getEmail())
                .claim("name", oAuth2User.getName())
                .claim("oauthId", oAuth2User.getOauthId())
                .claim("role", oAuth2User.getRole())
                .claim("registrationId", oAuth2User.getRegistrationId())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

}