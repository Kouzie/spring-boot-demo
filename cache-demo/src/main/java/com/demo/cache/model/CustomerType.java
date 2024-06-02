package com.demo.cache.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum CustomerType {
    BRONZE, SILVER, GOLD, DIAMOND;

    @JsonValue
    @Override
    public String toString() {
        return name();
    }


    @JsonCreator
    public static CustomerType forValue(String name) {
        for (CustomerType value : values()) {
            if (value.name().equals(name))
                return value;
        }
        return null;
    }
}