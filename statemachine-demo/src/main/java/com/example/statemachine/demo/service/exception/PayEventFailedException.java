package com.example.statemachine.demo.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PayEventFailedException extends Throwable {
    private String paymentId;
}
