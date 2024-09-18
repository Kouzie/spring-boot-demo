package com.example.auth.server.demo.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ClientRegistrationResponse(
        @JsonProperty("registration_access_token") String registrationAccessToken,
        @JsonProperty("registration_client_uri") String registrationClientUri,
        @JsonProperty("client_name") String clientName,
        @JsonProperty("client_id") String clientId,
        @JsonProperty("client_secret") String clientSecret,
        @JsonProperty("grant_types") List<String> grantTypes,
        @JsonProperty("redirect_uris") List<String> redirectUris,
        @JsonProperty("logo_uri") String logoUri,
        List<String> contacts,
        String scope) {
}
