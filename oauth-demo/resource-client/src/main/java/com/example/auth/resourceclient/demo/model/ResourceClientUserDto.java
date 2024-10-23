package com.example.auth.resourceclient.demo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceClientUserDto {
    private Long id;
    private String username;
    private String email;
    private String name;
    private String oauthId;
    private String role;
    private String registrationId;
}
