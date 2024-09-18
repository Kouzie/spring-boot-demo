package com.example.auth.server.demo.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ClientRegistrationRequest(
        @JsonProperty("client_name") String clientName,
        @JsonProperty("grant_types") List<String> grantTypes,
        @JsonProperty("redirect_uris") List<String> redirectUris,
        @JsonProperty("logo_uri") String logoUri,
        List<String> contacts,
        String scope) {
}
