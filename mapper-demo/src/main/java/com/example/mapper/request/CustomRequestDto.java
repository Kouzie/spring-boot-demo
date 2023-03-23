package com.example.mapper.request;

import com.example.mapper.config.resolver.ParamName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomRequestDto {
    @ParamName("my-name")
    private String myName;
}
