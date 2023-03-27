package com.example.jpa.model.id;

import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@RequiredArgsConstructor
public class OrderId implements Serializable {
    @Column(name = "order_number")
    private String number;

    public OrderId(String id) {
        this.number = id;
    }
}
