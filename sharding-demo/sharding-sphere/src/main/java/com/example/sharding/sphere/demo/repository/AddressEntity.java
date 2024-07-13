package com.example.sharding.sphere.demo.repository;

import com.example.sharding.sphere.demo.config.CustomIdGenerator;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import java.time.OffsetDateTime;

@Getter
@Entity
@Table(name = "t_address")
public class AddressEntity {
    @Id
    @GenericGenerator(name = "customIdGenerator",
            type = CustomIdGenerator.class)
    @GeneratedValue(generator = "customIdGenerator") // @GenericGenerator의 name modifier 에 지정한 이름
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
