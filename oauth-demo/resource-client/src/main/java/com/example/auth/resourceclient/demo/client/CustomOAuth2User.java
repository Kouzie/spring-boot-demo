package com.example.auth.resourceclient.demo.client;

import com.example.auth.resourceclient.demo.model.ResourceClientUserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final ResourceClientUserDto userDto;

    public CustomOAuth2User(ResourceClientUserDto userDto) {
        this.userDto = userDto;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userDto.getRole()));
    }

    @Override
    public String getName() {
        return this.userDto.getName();
    }
}
