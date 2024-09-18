package com.example.auth.resourceclient.demo.client;

import com.example.auth.resourceclient.demo.model.ResourceClientUserDto;
import com.example.auth.resourceclient.demo.model.ResourceClientUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class KakaoOidc2UserService extends OidcUserService {
    private final static String registrationId = "kakao-auth-registration-id";
    private final ResourceClientUserService resourceClientUserService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        if (!userRequest.getClientRegistration().getRegistrationId().equals(registrationId)) {
            return null;
        }
        log.info("kakao loadUser invoked, request:{}", userRequest.toString());
        OidcUser oidcUser = super.loadUser(userRequest);
        Map<String, Object> claims = oidcUser.getClaims();
        String nickname = claims.getOrDefault("nickname", "unknown_nickname").toString();
        String email = claims.getOrDefault("email", "unknown_email").toString();
        String name = oidcUser.getName();
        String role = "ROLE_USER";
        CustomOAuth2User customOAuth2User =  new CustomOAuth2User(nickname,
                email,
                name,
                role,
                registrationId,
                oidcUser);
        // 회원가입 upsert
        ResourceClientUserDto user = resourceClientUserService.upsertUser(customOAuth2User);
        return customOAuth2User;
    }
}
