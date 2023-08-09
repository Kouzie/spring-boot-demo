package com.example.jpa.model.order;

import com.example.jpa.model.id.ProductId;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.NamedQuery;

@Embeddable
@NamedQuery(
        name = "OrderLine.findByOrderNumber",
        query = "SELECT ol FROM OrderLine ol WHERE ol.order_number = :order_number FOR UPDATE"
)
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
