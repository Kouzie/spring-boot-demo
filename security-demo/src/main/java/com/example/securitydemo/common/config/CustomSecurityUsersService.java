package com.example.securitydemo.common.config;

import com.example.securitydemo.common.model.Member;
import com.example.securitydemo.common.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomSecurityUsersService implements UserDetailsService {

    private final MemberService memberRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    private void init() {
        if (memberRepository.findByUname("basic").isEmpty()) {
            memberRepository.save(new Member("basic", passwordEncoder.encode("basic"), "BASIC"));
        }
        if (memberRepository.findByUname("manager").isEmpty()) {
            memberRepository.save(new Member("manager", passwordEncoder.encode("manager"), "MANAGER"));
        }
        if (memberRepository.findByUname("admin").isEmpty()) {
            memberRepository.save(new Member("admin", passwordEncoder.encode("admin"), "ADMIN"));
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("CustomSecurityUsersService loadUserByUsername invoked, username:{}", username);
        Member member = memberRepository.findByUname(username)
                .orElseThrow(() -> new IllegalArgumentException());
        return new CustomSecurityUser(member);
    }
}