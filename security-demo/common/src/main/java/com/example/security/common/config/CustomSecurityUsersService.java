package com.example.security.common.config;

import com.example.security.common.model.MemberAuthEntity;
import com.example.security.common.model.MemberEntity;
import com.example.security.common.service.MemberAuthService;
import com.example.security.common.service.MemberService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomSecurityUsersService implements UserDetailsService {

    private final MemberService memberService;
    private final MemberAuthService memberAuthService;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    private void init() {
        if (memberService.findByUname("basic").isEmpty()) {
            memberService.save(new MemberEntity(
                    "basic",
                    passwordEncoder.encode("basic"),
                    MemberRole.BASIC
            ));
        }
        if (memberService.findByUname("manager").isEmpty()) {
            memberService.save(new MemberEntity(
                    "manager",
                    passwordEncoder.encode("manager"),
                    MemberRole.MANAGER
            ));
        }
        if (memberService.findByUname("admin").isEmpty()) {
            memberService.save(new MemberEntity(
                    "admin",
                    passwordEncoder.encode("admin"),
                    MemberRole.ADMIN
            ));
        }
        for (MemberRole role : MemberRole.values()) {
            Set<MemberAuth> auths = Set.of(MemberAuth.READ_PRIVILEGE, MemberAuth.WRITE_PRIVILEGE, MemberAuth.DELETE_PRIVILEGE);
            MemberAuthEntity memberAuth = memberAuthService.save(new MemberAuthEntity(role, auths));
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("CustomSecurityUsersService loadUserByUsername invoked, username:{}", username);
        MemberEntity member = memberService.findByUname(username).orElseThrow(IllegalArgumentException::new);
        MemberAuthEntity memberAuth = memberAuthService.findById(member.getRole());
        return new CustomSecurityUser(member, memberAuth);
    }
}