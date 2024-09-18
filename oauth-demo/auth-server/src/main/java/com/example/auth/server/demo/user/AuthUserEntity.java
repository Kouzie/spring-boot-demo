package com.example.auth.server.demo.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_entity")
public class AuthUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;
    private String uname;
    private String upw;
    private String email;
    private String role;

    @CreationTimestamp
    private LocalDateTime regdate;
    @UpdateTimestamp
    private LocalDateTime updatedate;

    public AuthUserEntity(String uname, String upw, String email, String role) {
        this.uname = uname;
        this.upw = upw;
        this.email = email;
        this.role = role;
    }

    protected AuthUserEntity() {
    }
}