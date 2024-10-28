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
    @Column(unique = true, nullable = false)
    private String uname;
    @Column(nullable = false)
    private String upw;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String role;

    @UpdateTimestamp
    private LocalDateTime updatedate;
    @CreationTimestamp
    private LocalDateTime regdate;

    public AuthUserEntity(String uname, String upw, String email, String role) {
        this.uname = uname;
        this.upw = upw;
        this.email = email;
        this.role = role;
    }

    protected AuthUserEntity() {
    }
}