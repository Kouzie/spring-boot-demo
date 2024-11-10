package com.example.jpa.order.model;


import com.example.jpa.order.OrderDto;
import com.example.jpa.order.model.id.OrderId;
import com.example.jpa.order.model.id.ProductId;
import lombok.Getter;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.jpa.JpaDempApplication.random;

@Getter
@Access(AccessType.FIELD)
@Entity
@Table(name = "purchase_order")
public class Order {

    @EmbeddedId
    private OrderId orderId;

//    @Embedded
//    private Orderer orderer;


    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "order_line", joinColumns = @JoinColumn(name = "order_number"))
    private List<OrderLine> orderLines;

//    @Embedded
//    private ShippingInfo shippingInfo;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private OrderState state;
    //
//    @Column(name = "order_date")
//    private LocalDateTime orderDate;
//
    @Version
    private long version;

    protected Order() {
    }

    public static Order random() {
        Order order = new Order();
        List<OrderLine> orderLines = new ArrayList<>();
        int count = random.nextInt(3);
        for (int i = 0; i < count; i++) {
            OrderLine ol = new OrderLine(
                    new ProductId(UUID.randomUUID().toString()),
                    random.nextInt(10) * 1000,
                    random.nextInt(99));
            orderLines.add(ol);
        }
        order.orderId = new OrderId(UUID.randomUUID().toString());
        order.orderLines = orderLines;
        order.state = OrderState.PREPARING;
        order.version = 0;
        return order;
    }

    public void update(OrderDto orderDto) {
        this.state = orderDto.getState();
    }
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