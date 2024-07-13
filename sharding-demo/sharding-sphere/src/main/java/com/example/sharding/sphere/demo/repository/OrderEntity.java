package com.example.sharding.sphere.demo.repository;

import com.example.sharding.sphere.demo.config.CustomIdGenerator;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import java.time.OffsetDateTime;

@Getter
@Entity
@Table(name = "t_order")
public class OrderEntity {
    @Id
    @GenericGenerator(name = "customIdGenerator",
            type = CustomIdGenerator.class)
    @GeneratedValue(generator = "customIdGenerator") // @GenericGenerator의 name modifier 에 지정한 이름
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
