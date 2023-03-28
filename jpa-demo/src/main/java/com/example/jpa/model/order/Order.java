package com.example.jpa.model.order;


import com.example.jpa.model.id.OrderId;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Access(AccessType.FIELD)
@Entity
@Table(name = "purchase_order")
public class Order {

    @EmbeddedId
    private OrderId number;

//    @Embedded
//    private Orderer orderer;
//
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "order_line", joinColumns = @JoinColumn(name = "order_number"))
    private List<OrderLine> orderLines;

//    @Embedded
//    private ShippingInfo shippingInfo;
//
//    @Column(name = "state")
//    @Enumerated(EnumType.STRING)
//    private OrderState state;
//
//    @Column(name = "order_date")
//    private LocalDateTime orderDate;
//
//    @Version
//    private long version;
}
/*
create table order_line
(
    order_number varchar(255) not null,
    price        integer,
    product_id   varchar(255),
    quantity     integer,
    line_idx     integer      not null,
    primary key (order_number, line_idx)
) engine = InnoDB;

create table purchase_order
(
    order_number      varchar(255) not null,
    order_date        datetime,
    orderer_id        varchar(255),
    orderer_name      varchar(255),
    shipping_addr1    varchar(255),
    shipping_addr2    varchar(255),
    shipping_zip_code varchar(255),
    shipping_message  varchar(255),
    receiver_name     varchar(255),
    receiver_phone    varchar(255),
    state             varchar(255),
    version           bigint       not null,
    primary key (order_number)
) engine = InnoDB;
*/