package com.example.mapper.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDataDto {
    // resources/ValidationMessages.properties 확인
    @NotEmpty(message = "{user.id.notempty}")
    private String id;
    @NotEmpty(message = "{user.name.notempty}")
    private String name;
    @NotNull(message = "{user.age.notnull}")
    private Integer age;
}
