package com.example.auth.resourceclient.demo.model;

import com.example.auth.resourceclient.demo.client.CustomOAuth2User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.ToString;

@Getter
@Entity
@ToString
public class ResourceClientUserEntity {
    @Id
    private String oauthId;
    private String name;
    private String email;
    private String nickname;
    private String role;
    private String registrationId;

    protected ResourceClientUserEntity() {
    }

    public ResourceClientUserEntity(CustomOAuth2User oAuth2User) {
        this.oauthId = oAuth2User.getOauthId();
        this.name = oAuth2User.getName();
        this.email = oAuth2User.getEmail();
        this.nickname = oAuth2User.getNickname();
        this.role = oAuth2User.getRole();
        this.registrationId = oAuth2User.getRegistrationId();
    }
}
