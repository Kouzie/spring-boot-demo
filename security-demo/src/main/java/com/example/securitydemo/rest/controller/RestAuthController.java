package com.example.securitydemo.rest.controller;

import com.example.securitydemo.common.config.CustomSecurityUser;
import com.example.securitydemo.rest.config.JwtTokenUtil;
import com.example.securitydemo.rest.dto.LoginRequestDto;
import com.example.securitydemo.rest.dto.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Profile("rest")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class RestAuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @PostMapping("/login_demo")
    public LoginResponseDto login(@RequestBody LoginRequestDto requestDto) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword());
        authentication = authenticationManagerBuilder.getObject().authenticate(authentication);
        String jwtToken = JwtTokenUtil.generateToken((CustomSecurityUser) authentication.getPrincipal());
        return new LoginResponseDto(jwtToken);
    }
}