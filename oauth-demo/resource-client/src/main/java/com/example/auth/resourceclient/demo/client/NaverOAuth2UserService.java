package com.example.auth.resourceclient.demo.client;

import com.example.auth.resourceclient.demo.model.ResourceClientUserDto;
import com.example.auth.resourceclient.demo.model.ResourceClientUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;


@Slf4j
@RequiredArgsConstructor
public class NaverOAuth2UserService extends DefaultOAuth2UserService {
    private final static String registrationId = "naver-auth-registration-id";
    private final ResourceClientUserService resourceClientUserService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        if (!userRequest.getClientRegistration().getRegistrationId().equals(registrationId)) {
            return null;
        }
        log.info("naver loadUser invoked, request:{}", userRequest.toString());
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // repsonse 로 감싸져있어 DefaultOAuth2UserService 를 사용하지 못하고 한꺼풀 벗기는 용으로 사용
        Map<String, Object> attributes = (Map<String, Object>) oAuth2User.getAttributes().get("response");
        // naver 는 nickname 을 name 으로 사용함
        String nickname = attributes.getOrDefault("name", "unknown_nickname").toString();
        String email = attributes.getOrDefault("email", "unknown_email").toString();
        // naver 에서는 계정을 email 로 사용
        String name = attributes.getOrDefault("username", email).toString();
        String role = "ROLE_USER";
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(nickname,
                email,
                name,
                role,
                registrationId,
                oAuth2User);
        // 회원가입 upsert
        ResourceClientUserDto user = resourceClientUserService.upsertUser(customOAuth2User);
        // 단순 로그인처리만 진행할거라면 oAuth2User 를 그대로 반환해도 상관없음.
        return customOAuth2User;
    }
}
