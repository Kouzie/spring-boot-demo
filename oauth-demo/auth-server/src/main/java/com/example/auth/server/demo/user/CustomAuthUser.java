package com.example.auth.server.demo.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class CustomAuthUser extends User {

    private final Long uid;
    private final String uname;

    // create by login
    public CustomAuthUser(AuthUserEntity member) {
        super(member.getUname(), member.getUpw(), new ArrayList<>());
        this.uid = member.getUid();
        this.uname = member.getUname();
    }

    // create by jwt
    public CustomAuthUser(Long uid, String subject, List<String> roles) {
        super(subject, "", roles.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList()));
        this.uid = uid;
        this.uname = subject;
    }

    public Map<String, Object> getClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", uid);
        claims.put("uname", uname);
        claims.put("roles", getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return claims;
    }
}
