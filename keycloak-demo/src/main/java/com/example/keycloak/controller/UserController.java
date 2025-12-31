package com.example.keycloak.controller;

import com.example.keycloak.adapter.KeycloakAdapter;
import com.example.keycloak.dto.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final KeycloakAdapter keycloakAdapter;
    private final OAuth2AuthorizedClientService authorizedClientService;

    /**
     * 사용자 정보를 조회하는 API
     * 
     * Access Token의 흐름:
     * 1. 사용자가 Keycloak으로 로그인
     * 2. OAuth2 Authorization Code Flow를 통해 Access Token 발급
     * 3. Spring Security가 Access Token을 HttpSession(메모리)에 저장
     * 4. 이 메서드 호출 시, 현재 요청의 JSESSIONID 쿠키를 통해 세션에서 Access Token 조회
     * 5. Access Token을 사용하여 Keycloak OIDC UserInfo Endpoint 호출
     * 
     * @AuthenticationPrincipal: 현재 인증된 사용자 정보 (OIDC ID Token의 Claims 포함)
     * 
     * 주의: @RegisteredOAuth2AuthorizedClient는 자동으로 token refresh를 시도하는데,
     * refresh token이 만료되면 401 에러가 발생할 수 있으므로, 
     * OAuth2AuthorizedClientService를 직접 사용하여 refresh를 우회합니다.
     */
    @GetMapping("/info")
    public UserInfoResponse getUserInfo(@AuthenticationPrincipal OidcUser principal, Authentication authentication) {
        if (principal == null) {
            return UserInfoResponse.error("User not found");
        }
        try {
            String username = principal.getPreferredUsername();
            // OAuth2AuthorizedClientService를 사용하여 access token 직접 조회 (refresh 우회)
            if (!(authentication instanceof OAuth2AuthenticationToken)) {
                return UserInfoResponse.error("Invalid authentication type");
            }
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            String clientRegistrationId = oauth2Token.getAuthorizedClientRegistrationId();
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(clientRegistrationId, oauth2Token.getName());
            if (client == null || client.getAccessToken() == null) {
                return UserInfoResponse.error("Access token not found. Please login again.");
            }
            
            String accessToken = client.getAccessToken().getTokenValue();
            String realm = clientRegistrationId;

            log.info("Fetching info for user: {} in realm: {} using OIDC UserInfo Endpoint", username, realm);
            
            // OIDC UserInfo Endpoint 호출 - 권한 불필요, 자신의 정보만 조회
            Map<String, Object> userInfo = keycloakAdapter.getUserInfo(realm, accessToken);

            return UserInfoResponse.builder()
                    .userInfo(userInfo)
                    .build();

        } catch (Exception e) {
            log.error("Error in getUserInfo", e);
            return UserInfoResponse.error("Internal error: " + e.getMessage());
        }
    }
}
