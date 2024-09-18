package com.example.auth.resourceclient.demo.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
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
    public void upsertNaverUser(OAuth2User oAuth2User, String registrationId) {
        Map<String, Object> response = oAuth2User.getAttribute("response");
        assert response != null;
        String email = response.get("email").toString();
        String username = email;
        String name = response.get("name").toString();
        String role = "ROLE_USER";
        String oauthId = response.get("id").toString();
        ResourceClientUserEntity entity = new ResourceClientUserEntity(
                username, email, name, oauthId, role, registrationId
        );
        entity = repository.save(entity);
        log.info("upsert user, id:{}, username:{}, registrationId:{}", entity.getId(), username, registrationId);
    }

    @Transactional
    public void upsertKakaoUser(OidcUser oidcUser, String registrationId) {
        String email = oidcUser.getClaimAsString("email"); // email 은 kakao 앱 인증받고 수신 가능
        String username = email;
        String name = oidcUser.getClaimAsString("nickname");
        String role = "ROLE_USER";
        String oauthId = oidcUser.getClaimAsString("sub"); // kakao 회원번호
        ResourceClientUserEntity entity = new ResourceClientUserEntity(
                username, email, name, oauthId, role, registrationId
        );
        entity = repository.save(entity);
        log.info("upsert user, id:{}, username:{}, registrationId:{}", entity.getId(), username, registrationId);
    }
}