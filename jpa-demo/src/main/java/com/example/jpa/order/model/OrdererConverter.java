package com.example.jpa.order.model;

import com.example.jpa.config.ObjectConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class OrdererConverter extends ObjectConverter<Orderer> {
    public OrdererConverter(ObjectMapper mapper) {
        super(mapper, Orderer.class);
    }
}
