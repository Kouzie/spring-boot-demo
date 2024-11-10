package com.example.jpa.order.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OrderState {
    PAYMENT_WAITING, PREPARING, SHIPPED, DELIVERING, DELIVERY_COMPLETED, CANCELED
    ;

    @JsonCreator
    public static OrderState forValue(String name) {
        for (OrderState value : values()) {
            if (value.name().equals(name))
                return value;
        }
        return null;
    }
}
