package com.example.auth.resourceserver.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_detail_entity")
public class AuthUserDetailEntity {
    @Id
    @Column(unique = true, nullable = false)
    private String uname;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private String gender;
    @Column(nullable = false)
    private String nickname;
    @Column(nullable = false)
    private String birth;

    public AuthUserDetailEntity(String uname, String phone, String gender, String nickname, String birth) {
        this.uname = uname;
        this.phone = phone;
        this.gender = gender;
        this.nickname = nickname;
        this.birth = birth;
    }

    protected AuthUserDetailEntity() {
    }
}