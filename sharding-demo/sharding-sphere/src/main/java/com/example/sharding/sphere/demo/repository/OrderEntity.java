package com.example.sharding.sphere.demo.repository;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Entity(name = "t_order")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 기본적으로 GenerationType.IDENTITY를 사용합니다.
    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "account_id")
    private Long accountId;
    private String title;
    private OffsetDateTime createTime;

    protected OrderEntity() {
    }

    public OrderEntity(String title, Long accountId) {
        this.title = title;
        this.accountId = accountId;
        this.createTime = OffsetDateTime.now();
    }
}
