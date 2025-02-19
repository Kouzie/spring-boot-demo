package com.example.localmessage.controller;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDataDto {
    @NotEmpty
    private String id;
    @NotEmpty
    private String name;
    @NotNull
    private Integer age;
}
