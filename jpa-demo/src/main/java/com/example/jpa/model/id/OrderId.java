package com.example.jpa.model.id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class OrderId implements Serializable {
    @Column(name = "order_number")
    private String number;
}
