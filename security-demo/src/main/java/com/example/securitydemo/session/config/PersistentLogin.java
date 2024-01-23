package com.example.securitydemo.session.config;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
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