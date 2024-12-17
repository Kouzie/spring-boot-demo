package com.example.mongodb.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequestDto {
    private String username;
    private String email;
    private Integer age;
    private String gender;
    private String nickname;
    private String desc;
}
