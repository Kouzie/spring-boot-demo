package com.example.auth.server.demo.user;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;

/**
 * Example service to perform lookup of user info for customizing an {@code id_token}.
 */
@Service
@RequiredArgsConstructor
public class CustomOidcUserInfoService {

    private final AuthUserService authUserService;

    public OidcUserInfo loadUser(String uname) {
        AuthUserEntity entity = authUserService.findByUname(uname).orElseThrow();
        return OidcUserInfo.builder()
                .subject(uname)
                .name(uname)
                .nickname(entity.getNickname())
                .email(entity.getEmail())
                .updatedAt(entity.getUpdatedate().format(DateTimeFormatter.ISO_DATE))
                .claim("uid", entity.getUid().toString())
                .claim("role", entity.getRole())
                .claim("ragdate", entity.getRegdate().format(DateTimeFormatter.ISO_DATE))
                .build();
    }
}