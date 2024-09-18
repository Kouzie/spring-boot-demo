package com.example.auth.server.demo.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientRegistrationResponse {
    @JsonProperty("id")
    String id;
    @JsonProperty("registration_access_token")
    String registrationAccessToken;
    @JsonProperty("registration_client_uri")
    String registrationClientUri;
    @JsonProperty("client_id")
    String clientId;
    @JsonProperty("client_secret")
    String clientSecret;
    @JsonProperty("client_name")
    String clientName;
    @JsonProperty("authorization_grant_types")
    Set<String> authorizationGrantTypes;
    @JsonProperty("redirect_uri")
    String redirectUri;
    @JsonProperty("post_logout_redirect_uri")
    String postLogoutRedirectUri;
    @JsonProperty("logo_uri")
    String logoUri;
    @JsonProperty("scopes")
    Set<String> scopes;
}
