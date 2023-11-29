package com.example.mapper.validator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CustomEnum {
    INFO, WARN, ERROR, DEBUG;

    @JsonValue
    public String getValue() {
        return this.name();
    }

    @JsonCreator
    public static CustomEnum fromString(String value) {
        for (CustomEnum customEnum : values()) {
            if (customEnum.name().equals(value))
                return customEnum;
        }
        return null;
    }
}
