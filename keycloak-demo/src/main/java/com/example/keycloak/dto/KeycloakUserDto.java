package com.example.keycloak.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeycloakUserDto {
    private String id;
    private Long createdTimestamp;
    private boolean enabled;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> realmRoles;
    private Map<String, Object> clientRoles;
    private Map<String, Boolean> access;
}
