package com.example.statemachine.demo.service.exception;

public class PrepareFailedException extends RuntimeException {
    public PrepareFailedException(String msg) {
        super(msg);
    }
}
