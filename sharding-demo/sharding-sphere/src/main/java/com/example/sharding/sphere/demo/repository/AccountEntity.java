package com.example.sharding.sphere.demo.repository;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Entity(name = "t_account")
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 기본적으로 GenerationType.IDENTITY를 사용합니다.
    @Column(name = "account_id")
    private Long accountId;
    private String userName;
    private OffsetDateTime createTime;

    protected AccountEntity() {
    }

    public AccountEntity( String userName) {
        this.userName = userName;
        this.createTime = OffsetDateTime.now();
    }
}
