package com.example.auth.resourceclient.demo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceClientUserDto {
    private String oauthId;
    private String name;
    private String email;
    private String nickname;
    private String role;
    private String registrationId;
}
