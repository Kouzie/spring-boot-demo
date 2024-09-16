package com.example.securitydemo.rest.config;

import com.example.securitydemo.common.config.CustomSecurityUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public class JwtTokenUtil implements Serializable {

    public static final long JWT_TOKEN_VALIDITY_SEC = 5 * 60; //5분
    public static Random random = new Random();
    private static SecretKey secretKey;


    static {
        byte[] data = new byte[255];
        random.nextBytes(data);
        String secret = "tM6S1ERulKlPWSsvzZa3Kun9vpH3YbikZpospKYhYS97vtUKiNDFFXFnyTqJX1bL";
        secretKey = Keys.hmacShaKeyFor(secret.getBytes()); //or HS384 or HS512
    }

    // jwt 생성
    public static String generateToken(CustomSecurityUser customSecurityUser) {
        Map<String, Object> claims = customSecurityUser.getClaims();
        String subject = customSecurityUser.getUsername();
        long currentTimeMillis = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(currentTimeMillis))
                .setExpiration(new Date(currentTimeMillis + JWT_TOKEN_VALIDITY_SEC * 1000))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    // Claims expiration date 혹은 username 를 가져오기 위해 호출
    // 모든 값은 verify signature 부분에서 가져온다.
    private static <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //jwt 로부터 username get
    private static String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    //jwt 로부터 exp get
    private static Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // 토큰의 각종 데이터를 되찾아 오기 위해 시크릿 키가 필요
    public static Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰 시간 초과 확인
    public static Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // validate token
    public static Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}