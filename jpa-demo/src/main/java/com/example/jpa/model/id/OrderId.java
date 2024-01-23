package com.example.jpa.model.id;

import lombok.AllArgsConstructor;
import lombok.Getter;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Embeddable
@AllArgsConstructor
public class OrderId implements Serializable {
    @Column(name = "order_number")
    private String number;

    protected OrderId() {

    }
}
