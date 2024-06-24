package com.example.sharding.sphere.demo.repository;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Entity
@Table(name = "t_order_item", indexes = {@Index(name = "order_id_index", columnList = "orderId")})
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 기본적으로 GenerationType.IDENTITY를 사용합니다.
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
