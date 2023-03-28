package com.example.swagger.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestResponse {
    private String msg;
    private String code;

    public TestResponse(String msg, String code) {
        this.msg = msg;
        this.code = code;
    }
    protected TestResponse() {

    }
}
