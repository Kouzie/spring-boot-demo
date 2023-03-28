package com.example.securitydemo.rest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {

    private final String jwtToken;

    public LoginResponseDto(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
