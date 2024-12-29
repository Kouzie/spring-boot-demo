package com.example.security.common.config;

import com.example.security.common.model.MemberAuthEntity;
import com.example.security.common.model.MemberEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class CustomSecurityUser extends User {

    private static final String ROLE_PREFIX = "ROLE_";

    private final Long uid;
    private final String uname;
    private final String role;

    // create by login
    public CustomSecurityUser(MemberEntity member, MemberAuthEntity memberAuth) {
        super(member.getUname(), member.getUpw(), makeGrantedAuth(member.getRole(), memberAuth.getAuths()));
        this.uid = member.getUid();
        this.uname = member.getUname();
        this.role = member.getRole().name();
    }

    // create by jwt
    public CustomSecurityUser(Long uid, String subject, String role, Set<String> auths) {
        super(subject, "", makeGrantedAuth(role, auths));
        this.uid = uid;
        this.uname = subject;
        this.role = role;
    }

    // make auth from login
    private static List<GrantedAuthority> makeGrantedAuth(MemberRole role, Set<MemberAuth> auths) {
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority(ROLE_PREFIX + role.name()));
        auths.forEach(memberAuths -> list.add(new SimpleGrantedAuthority(memberAuths.name())));
        return list;
    }

    // make auth from jwt
    private static List<GrantedAuthority> makeGrantedAuth(String role, Set<String> auths) {
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority(ROLE_PREFIX + role));
        auths.forEach(memberAuths -> list.add(new SimpleGrantedAuthority(memberAuths)));
        return list;
    }

    public Map<String, Object> getClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", uid);
        claims.put("uname", uname);
        claims.put("role", role);
        claims.put("auths", getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return claims;
    }


    public static Authentication getAuthentication(Map<String, Object> claims) {
        Long uid = Long.valueOf(claims.getOrDefault("uid", 0).toString());
        String subject = claims.getOrDefault("sub", "").toString();
        String role = claims.getOrDefault("role", "").toString();
        Set<String> auths = Set.copyOf((List<String>) claims.get("auths"));
        UserDetails userDetails = new CustomSecurityUser(uid, subject, role, auths);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
