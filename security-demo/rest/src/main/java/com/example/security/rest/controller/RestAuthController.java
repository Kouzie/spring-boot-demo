package com.example.security.rest.controller;

import com.example.security.common.config.CustomSecurityUser;
import com.example.security.rest.config.JwtTokenUtil;
import com.example.security.rest.dto.LoginRequestDto;
import com.example.security.rest.dto.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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