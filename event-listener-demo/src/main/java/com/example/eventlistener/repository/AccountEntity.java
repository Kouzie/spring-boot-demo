package com.example.eventlistener.repository;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Getter
@Entity
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "account_id")
    private Long accountId;
    private String name;
    private String username;
    private String email;
    @CreationTimestamp
    private OffsetDateTime created;
    @UpdateTimestamp
    private OffsetDateTime updated;

    protected AccountEntity() {
    }

    public AccountEntity(String name, String username, String email) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.created = OffsetDateTime.now();
        this.updated = OffsetDateTime.now();
    }
}
