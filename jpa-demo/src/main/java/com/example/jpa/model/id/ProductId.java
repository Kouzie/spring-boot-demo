package com.example.jpa.model.id;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Access(AccessType.FIELD)
public class ProductId implements Serializable {
    @Column(name = "product_id")
    private String id;
}