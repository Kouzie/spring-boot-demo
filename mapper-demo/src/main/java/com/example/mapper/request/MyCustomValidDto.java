package com.example.mapper.request;

import com.example.mapper.validator.ValidCustomEnum;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class MyCustomValidDto {
    @AssertFalse
    private Boolean isFalse;

    @NotBlank(message = "should be not null")
    private String msg1;

    @ValidCustomEnum
    private String enumValue;
}
