package com.example.security.session.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "persistent_logins")
public class PersistentLogin {
    @Id
    private String series;

    private String username;
    private String token;
    private LocalDateTime lastUsed;
}