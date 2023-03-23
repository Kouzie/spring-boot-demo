package com.example.aws.paramstore;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ParamStore {
    @Value("${TEST_VALUE}")
    private String profileValue = "test";
}
