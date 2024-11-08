package com.example.statemachine.demo.adaptor;

import com.example.statemachine.demo.adaptor.exception.PayFailedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PayAdaptor {
    public String pay(String orderId) throws PayFailedException {
        return UUID.randomUUID().toString();
    }

    public String payCancel(String paymentId) {
        return UUID.randomUUID().toString();
    }
}
