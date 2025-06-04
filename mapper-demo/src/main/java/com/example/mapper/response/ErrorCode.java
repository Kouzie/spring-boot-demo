package com.example.mapper.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    BAD_REQUEST("400", "BadRequest"),
    PAGE_NOT_FOUND("404", "PageNotFound"),
    METHOD_NOT_ALLOW("405", "MethodNotAllow"),
    SERVER_ERROR("500", "ServerError"),
    VALIDATION_ERROR("422", "ValidationError"),
    ;

    private final String code;
    private final String error;
}
