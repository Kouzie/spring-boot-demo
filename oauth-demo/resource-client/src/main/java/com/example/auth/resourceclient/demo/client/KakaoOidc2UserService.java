package com.example.auth.resourceclient.demo.client;

import com.example.auth.resourceclient.demo.model.ResourceClientUserDto;
import com.example.auth.resourceclient.demo.model.ResourceClientUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

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
        // 회원가입 upsert
        ResourceClientUserDto user = resourceClientUserService.upsertKakaoUser(oidcUser, registrationId);
        return new CustomOAuth2User(user);
    }
}
