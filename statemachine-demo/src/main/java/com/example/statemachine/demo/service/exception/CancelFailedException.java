package com.example.statemachine.demo.service.exception;

public class CancelFailedException extends RuntimeException {
    public CancelFailedException(String msg) {
        super(msg);
    }
}
