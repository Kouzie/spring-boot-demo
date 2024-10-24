package com.example.auth.server.demo.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClientRegistrationRequest {
    String id;
    @JsonProperty("client_id")
    String clientId;
    @JsonProperty("client_secret")
    String clientSecret;
    @JsonProperty("client_name")
    String clientName;
    @JsonProperty("redirect_uri")
    String redirectUri;
    @JsonProperty("post_logout_redirect_uri")
    String postLogoutRedirectUri;
    @JsonProperty("logo_uri")
    String logoUri;
    @JsonProperty("scopes")
    List<String> scopes;
}
