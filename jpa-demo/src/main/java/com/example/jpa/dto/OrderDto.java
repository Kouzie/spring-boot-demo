package com.example.jpa.dto;

import com.example.jpa.model.order.OrderState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {
    private String orderId;
    private OrderState state;
    private Long version;

    protected OrderDto () {

    }

    public OrderDto(String id, OrderState state, Long version) {
        this.orderId = id;
        this.state = state;
        this.version = version;
    }
}
