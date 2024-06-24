package com.example.sharding.sphere.demo.repository;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Entity
@Table(name = "t_address")
public class AddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 기본적으로 GenerationType.IDENTITY를 사용합니다.
    @Column(name = "address_id")
    private Long addressId;
    @Column(name = "address_name")
    private String addressName;

    protected AddressEntity() {
    }

    public AddressEntity(String addressName) {
        this.addressName = addressName;
    }
}
