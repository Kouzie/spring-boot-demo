package com.example.auth.server.demo.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY) // objectmapper map 변환과정에서 default constructor 가 없으면 발생하는 에러 조치용
public class CustomAuthUser extends User {

    private final Long uid;
    private final String uname;
    private final String email;

    // create by login
    public CustomAuthUser(AuthUserEntity member) {
        super(member.getUname(), member.getUpw(), generateAuthorities(member));
        this.uid = member.getUid();
        this.uname = member.getUname();
        this.email = member.getEmail();
    }

    private static Collection<? extends GrantedAuthority> generateAuthorities(AuthUserEntity member) {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getRole()));
        return authorities;
    }
}
