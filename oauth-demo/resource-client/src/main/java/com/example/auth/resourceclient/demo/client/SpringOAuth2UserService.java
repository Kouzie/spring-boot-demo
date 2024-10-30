package com.example.auth.resourceclient.demo.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;


@Slf4j
@RequiredArgsConstructor
public class SpringOAuth2UserService extends DefaultOAuth2UserService {
    private final static String registrationId = "oauth-demo-registration-id";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        if (!userRequest.getClientRegistration().getRegistrationId().equals(registrationId)) {
            return null;
        }
        log.info("spring loadUser invoked, request:{}", userRequest.toString());
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 회원가입 upsert 생략, 자체 서비스
        String nickname = oAuth2User.getAttribute("nickname");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getName();
        String role = "ROLE_USER";
        return new CustomOAuth2User(nickname,
                email,
                name,
                role,
                registrationId,
                oAuth2User);
    }
}
