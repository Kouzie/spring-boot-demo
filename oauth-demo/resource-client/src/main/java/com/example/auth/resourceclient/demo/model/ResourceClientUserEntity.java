package com.example.auth.resourceclient.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class ResourceClientUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String name;
    private String oauthId;
    private String role;
    private String registrationId;

    protected ResourceClientUserEntity() {
    }

    public ResourceClientUserEntity(String username, String email, String name, String oauthId, String role, String registrationId) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.oauthId = oauthId;
        this.role = role;
        this.registrationId = registrationId;
    }
}
