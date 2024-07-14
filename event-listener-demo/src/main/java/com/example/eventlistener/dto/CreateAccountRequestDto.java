package com.example.eventlistener.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountRequestDto {
    private String username;
    private String name;
    private String email;
}
