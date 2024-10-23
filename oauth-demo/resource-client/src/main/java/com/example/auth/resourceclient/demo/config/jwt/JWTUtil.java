package com.example.auth.resourceclient.demo.config.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.stream.Collectors;

@Component
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createJwt(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String role, long exp) {
//        "auth_time" -> {Instant@16685} "2024-10-23T10:25:25Z"
//        "iss" -> {URL@16686} "http://localhost:9090"
//        "exp" -> {Instant@16687} "2024-10-23T10:57:44Z"
//        "iat" -> {Instant@16688} "2024-10-23T10:27:44Z"
//        "sub" -> "admin"
//        "azp" -> "oauth-demo-client-id"
//        "nonce" -> "CyQ4FwvlNmPtoAI3P2PzcNdRF9Jv7E287BDN7uTImSs"
//        "jti" -> "28d79b5f-3987-4c47-9f6d-fdd49c123bc0"
//        "sid" -> "YvJThdQfk4Tzp70vIPTbtq3eBLcFw6qyBSz2fxT7zYM"
        String token = Jwts.builder()
                .claim("sub", attributes.get("sub"))
                .claim("azp", attributes.get("azp"))
                .claim("nonce", attributes.get("nonce"))
                .claim("jti", attributes.get("jti"))
                .claim("sid", attributes.get("sid"))
                .claim("authorities", authorities.stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + exp))
                .signWith(secretKey)
                .compact();
        return token;
    }

    public Map<String, Object> getClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    public Collection<? extends GrantedAuthority> getAuthorities(String token) {
        String authorities = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("authorities", String.class);
        return Arrays.stream(authorities.split(",")).map(authority -> new SimpleGrantedAuthority(authorities)).toList();
    }
}