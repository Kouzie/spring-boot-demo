package com.example.jpa.model.order;

import javax.persistence.*;

@Embeddable
public class ShippingInfo {
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "zipCode", column = @Column(name = "shipping_zip_code")),
            @AttributeOverride(name = "address1", column = @Column(name = "shipping_addr1")),
            @AttributeOverride(name = "address2", column = @Column(name = "shipping_addr2"))
    })
    private Address address;

    @Embedded
    private Receiver receiver;

    @Column(name = "shipping_message")
    private String message;
}
