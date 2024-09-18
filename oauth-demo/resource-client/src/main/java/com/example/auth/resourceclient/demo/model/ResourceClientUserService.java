package com.example.auth.resourceclient.demo.model;

import com.example.auth.resourceclient.demo.client.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceClientUserService {
    private final ResourceClientUserRepository repository;

    @Transactional
    public ResourceClientUserDto upsertUser(CustomOAuth2User oAuth2User) {
        ResourceClientUserEntity entity = new ResourceClientUserEntity(oAuth2User);
        entity = repository.save(entity);
        log.info("upsert user success, {}", entity);
        return toDto(entity);
    }

    public ResourceClientUserDto toDto(ResourceClientUserEntity entity) {
        ResourceClientUserDto dto = new ResourceClientUserDto();
        dto.setOauthId(entity.getOauthId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setNickname(entity.getNickname());
        dto.setRole(entity.getRole());
        dto.setRegistrationId(entity.getRegistrationId());
        return dto;
    }
}