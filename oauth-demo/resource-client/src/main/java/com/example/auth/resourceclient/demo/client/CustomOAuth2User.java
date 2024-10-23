package com.example.auth.resourceclient.demo.client;

import com.example.auth.resourceclient.demo.model.ResourceClientUserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomOAuth2User implements OidcUser {

    private String username;
    private String email;
    private String name;
    private String oauthId;
    private String role;
    private String registrationId;
    private Map<String, Object> attributes;
    private Collection<? extends GrantedAuthority> authorities;
    private OAuth2User oAuth2User;

    public CustomOAuth2User(ResourceClientUserDto user, OAuth2User oAuth2User) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.name = user.getName();
        this.oauthId = user.getOauthId();
        this.role = user.getRole();
        this.registrationId = user.getRegistrationId();

        this.attributes = oAuth2User.getAttributes();
        this.authorities = oAuth2User.getAuthorities();
        this.oAuth2User = oAuth2User;
    }

    public CustomOAuth2User(OidcUser oidcUser, String role, String registrationId) {
        this.username = oidcUser.getEmail();
        this.email = oidcUser.getEmail();
        this.name = oidcUser.getName();
        this.oauthId = null;
        this.role = role;
        this.registrationId = registrationId;
        this.oAuth2User = oidcUser;

        this.attributes = oAuth2User.getAttributes();
        this.authorities = oAuth2User.getAuthorities();
    }

    public CustomOAuth2User(Map<String, Object> attributes, String name) {
        this.username = (String) attributes.getOrDefault("name", "null");
        this.role = attributes.get("role").toString();

        this.attributes = attributes;
        this.authorities = List.of(new SimpleGrantedAuthority(this.role));
    }

    public String getRole() {
        return role;
    }

    @Override
    public Map<String, Object> getClaims() {
        return ((OidcUser) oAuth2User).getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return ((OidcUser) oAuth2User).getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return ((OidcUser) oAuth2User).getIdToken();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getName() {
        return oAuth2User.getName();
    }

    public String getUsername() {
        return username;
    }
}
