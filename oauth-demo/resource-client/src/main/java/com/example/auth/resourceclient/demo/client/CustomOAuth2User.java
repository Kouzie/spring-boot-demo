package com.example.auth.resourceclient.demo.client;


import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OidcUser {

    private final String nickname;
    private final String email;
    private final String name;
    private final String oauthId;
    private final String role;
    private final String registrationId;

    private final OAuth2User oAuth2User;

    public CustomOAuth2User(String nickname,
                            String email,
                            String name,
                            String role,
                            String registrationId,
                            OAuth2User oAuth2User) {
        this.nickname = nickname;
        this.email = email;
        this.name = name;
        this.role = role;
        this.registrationId = registrationId;
        this.oauthId = registrationId + "_" + name;
        this.oAuth2User = oAuth2User;
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
        return this.oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (oAuth2User == null)
            return List.of(new SimpleGrantedAuthority(role));
        else
            return this.oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return this.name;
    }
}
