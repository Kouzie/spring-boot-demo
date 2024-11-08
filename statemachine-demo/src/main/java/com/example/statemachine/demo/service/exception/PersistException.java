package com.example.statemachine.demo.service.exception;

public class PersistException extends RuntimeException {
    public PersistException(String msg) {
        super(msg);
    }
}
