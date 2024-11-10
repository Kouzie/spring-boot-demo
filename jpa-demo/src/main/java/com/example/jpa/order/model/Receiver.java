package com.example.jpa.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Receiver {
    @Column(name = "receiver_name")
    private String name;
    @Column(name = "receiver_phone")
    private String phone;
}