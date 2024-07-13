package com.example.sharding.sphere.demo.repository;

import com.example.sharding.sphere.demo.config.CustomIdGenerator;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import java.time.OffsetDateTime;

@Getter
@Entity
@Table(name = "t_order_item", indexes = {@Index(name = "order_id_index", columnList = "orderId")})
public class OrderItemEntity {
    @Id
    @GenericGenerator(name = "customIdGenerator",
            type = CustomIdGenerator.class)
    @GeneratedValue(generator = "customIdGenerator") // @GenericGenerator의 name modifier 에 지정한 이름
    @Column(name = "order_item_id")
    private Long orderItemId;
    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "account_id")
    private Long accountId;
    private String itemName;
    private OffsetDateTime createTime;

    protected OrderItemEntity() {
    }

    public OrderItemEntity(Long orderId, Long accountId, String itemName) {
        this.orderId = orderId;
        this.accountId = accountId;
        this.itemName = itemName;
        this.createTime = OffsetDateTime.now();
    }
}
