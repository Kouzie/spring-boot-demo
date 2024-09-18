package com.example.auth.resourceclient.demo.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@Slf4j
@RequiredArgsConstructor
public class SpringOidc2UserService extends OidcUserService {
    private final static String registrationId = "oauth-demo-registration-id";

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        if (!userRequest.getClientRegistration().getRegistrationId().equals(registrationId)) {
            return null;
        }
        log.info("spring loadUser invoked, request:{}", userRequest.toString());
        OidcUser oidcUser = super.loadUser(userRequest);
        // 회원가입 upsert 생략, 자체 서비스
        String nickname = oidcUser.getAttribute("nickname");
        String email = oidcUser.getAttribute("email");
        String name = oidcUser.getName();
        String role = "ROLE_USER";
        return new CustomOAuth2User(nickname,
                email,
                name,
                role,
                registrationId,
                oidcUser);
    }
}
