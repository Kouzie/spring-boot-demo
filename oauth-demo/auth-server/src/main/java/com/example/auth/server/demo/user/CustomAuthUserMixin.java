package com.example.auth.server.demo.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/*
objectMapper.readValue 로 아래와 같은 문자열을 읽으

{
    "@class": "com.example.auth.server.demo.user.CustomAuthUser",
    "username": "admin",
    "password": null,
    "authorities": ["java.util.Collections$UnmodifiableSet", [{
        "@class": "org.springframework.security.core.authority.SimpleGrantedAuthority",
        "authority": "admin"
    }]],
    "uid": 2,
    "uname": "admin",
    "email": "admin@test.com",
    "accountNonExpired": true,
    "accountNonLocked": true,
    "credentialsNonExpired": true,
    "enabled": true
},

"Jackson annotations or by providing a Mixin" 에러가 발생함

원래는 CustomAuthUser 위에 @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY) 어노테이션을 추가하였는데
CustomAuthUser 를 계속 security.core.userdetails.User 로 변환해서 mixin 용 클래스를 생성 및 objectMapper 에 적용하였음

Authorization entity 의 java.security.Principal 에 CustomAuthUser 가 들어갈것으로 기대했지만 변환과정에서 security.core.userdetails.User 로 변환되어 저장됨
원하는 포맷으로 저장하려면 JpaOAuth2AuthorizationService 에서 objectmapper 기반 저장 및 authentication 객체 생성방법 커스터마이징 필요
*/
public abstract class CustomAuthUserMixin {

    @JsonCreator
    public CustomAuthUserMixin(
            @JsonProperty("uid") Long uid,
            @JsonProperty("uname") String uname,
            @JsonProperty("email") String email,
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities) {
    }
}

