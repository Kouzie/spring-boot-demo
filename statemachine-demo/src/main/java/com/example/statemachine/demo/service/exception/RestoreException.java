package com.example.statemachine.demo.service.exception;

public class RestoreException extends RuntimeException {
    public RestoreException(String msg) {
        super(msg);
    }

    public RestoreException(Exception e) {
        super(e);
    }
}
