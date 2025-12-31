package com.example.keycloak.controller;

import com.example.keycloak.adapter.KeycloakAdapter;
import com.example.keycloak.dto.KeycloakUserDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class UserController {

    private final KeycloakAdapter keycloakAdapter;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public UserController(KeycloakAdapter keycloakAdapter, OAuth2AuthorizedClientService authorizedClientService) {
        this.keycloakAdapter = keycloakAdapter;
        this.authorizedClientService = authorizedClientService;
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal OidcUser principal, OAuth2AuthenticationToken token, Model model) {
        String username = principal.getPreferredUsername();
        String realm = token.getAuthorizedClientRegistrationId();

        model.addAttribute("username", username);
        model.addAttribute("email", principal.getEmail());
        model.addAttribute("claims", principal.getClaims());

        // Fetch user info from Keycloak Admin API
        try {
            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                    token.getAuthorizedClientRegistrationId(),
                    token.getName());
            String accessToken = authorizedClient.getAccessToken().getTokenValue();

            KeycloakUserDto user = keycloakAdapter.getUserInfo(realm, username, accessToken);
            if (user != null) {
                model.addAttribute("keycloakUser", user);
                model.addAttribute("createdDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new Date(user.getCreatedTimestamp())));
            }
        } catch (Exception e) {
            model.addAttribute("apiError", "Failed to fetch details from Keycloak API: " + e.getMessage());
        }

        return "profile";
    }
}
