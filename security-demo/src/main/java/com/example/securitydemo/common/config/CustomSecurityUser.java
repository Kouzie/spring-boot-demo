package com.example.securitydemo.common.config;

import com.example.securitydemo.common.model.Member;
import com.example.securitydemo.common.model.MemberRole;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class CustomSecurityUser extends User {

    private static final String ROLE_PREFIX = "ROLE_";

    private final Long uid;
    private final String uname;

    // create by login
    public CustomSecurityUser(Member member) {
        super(member.getUname(), member.getUpw(), makeGrantedAuth(member.getRoles()));
        this.uid = member.getUid();
        this.uname = member.getUname();
    }

    // create by jwt
    public CustomSecurityUser(Long uid, String subject, List<String> roles) {
        super(subject, "", roles.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList()));
        this.uid = uid;
        this.uname = subject;
    }

    // make auth from login
    private static List<GrantedAuthority> makeGrantedAuth(List<MemberRole> roles) {
        List<GrantedAuthority> list = new ArrayList<>();
        roles.forEach(memberRole ->
                list.add(new SimpleGrantedAuthority(ROLE_PREFIX + memberRole.getRoleName())));
        return list;
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
