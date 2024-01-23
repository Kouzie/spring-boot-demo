package com.example.jpa.model.order;

import com.example.jpa.model.id.ProductId;
import jakarta.persistence.*;

@Embeddable
public class OrderLine {

    @Embedded
    private ProductId productId;

    @Column(name = "price")
    private Integer price;

    @Column(name = "quantity")
    private Integer quantity;

    protected OrderLine() {

    }

    public OrderLine(ProductId productId, Integer price, Integer quantity) {
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
    }
}
