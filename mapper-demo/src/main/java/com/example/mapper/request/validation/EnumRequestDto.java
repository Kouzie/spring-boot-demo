package com.example.mapper.request.validation;

import com.example.mapper.config.validation.ValidEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnumRequestDto {
    @ValidEnum(target = TestType.class)
    private String type;
}
