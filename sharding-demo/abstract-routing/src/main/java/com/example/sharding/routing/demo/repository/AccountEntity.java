package com.example.sharding.routing.demo.repository;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Entity(name = "t_account")
public class AccountEntity {
    @Id
    @Column(name = "account_id")
    private Long accountId;
    private String userName;
    private OffsetDateTime createTime;

    protected AccountEntity() {
    }

    public AccountEntity(Long accountId, String userName) {
        this.accountId = accountId;
        this.userName = userName;
        this.createTime = OffsetDateTime.now();
    }
}
