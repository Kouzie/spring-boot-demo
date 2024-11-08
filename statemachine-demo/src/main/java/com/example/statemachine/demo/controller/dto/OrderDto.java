package com.example.statemachine.demo.controller.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderDto {
    private String orderId;
    private String state;
    private LocalDateTime created;
}
