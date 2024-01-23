package com.example.jpa.model.id;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Access(AccessType.FIELD)
public class ProductId implements Serializable {
    @Column(name = "product_id")
    private String id;

    protected ProductId() {
    }

    public ProductId(String id) {
        this.id = id;
    }
}