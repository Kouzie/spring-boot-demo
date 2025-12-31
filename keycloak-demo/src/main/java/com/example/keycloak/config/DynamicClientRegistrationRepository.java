package com.example.keycloak.config;

import com.example.keycloak.adapter.KeycloakAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 동적으로 Realm별 ClientRegistration을 생성하는 Repository
 * Realm 이름을 registrationId로 사용하며, PKCE 방식을 사용하는 public client로 구성
 * PKCE (Proof Key for Code Exchange) 방식을 사용하여 client secret 없이 안전하게 인증 수행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicClientRegistrationRepository implements ClientRegistrationRepository, ApplicationListener<ContextRefreshedEvent> {

    @Value("${keycloak.client-id}")
    private String defaultClientId;
    
    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;
    
    private final Map<String, ClientRegistration> registrations = new ConcurrentHashMap<>();
    private final KeycloakAdapter keycloakAdapter;

    /**
     * ContextRefreshedEvent를 사용하여 모든 빈이 초기화된 후에 실행
     */
    @Override
    public void onApplicationEvent(@org.springframework.lang.NonNull ContextRefreshedEvent event) {
        // 루트 컨텍스트에서만 실행 (중복 실행 방지)
        if (event.getApplicationContext().getParent() != null) {
            return;
        }
        
        try {
            log.info("Initializing DynamicClientRegistrationRepository - checking demo-client in master realm...");

            // master realm에 client가 존재하는지 확인 후 생성
            String accessToken = keycloakAdapter.getAdminCliAccessToken();
            boolean clientExists = keycloakAdapter.adminApiClientExists(accessToken, "master", defaultClientId);
            if (!clientExists) {
                keycloakAdapter.adminApiCreateClient(accessToken, "master", defaultClientId);
                log.info("Demo client '{}' initialization completed in master realm", defaultClientId);
            } else {
                log.info("Demo client '{}' already exists in master realm. Skipping creation.", defaultClientId);
            }
        } catch (Exception e) {
            log.warn("Failed to initialize client '{}' in master realm: {}. ", defaultClientId, e.getMessage());
        }
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        return registrations.computeIfAbsent(registrationId, this::createClientRegistration);
    }

    /**
     * Client Registration 생성 (모든 realm 공통)
     * PKCE 방식을 사용하는 public client로 생성 (clientSecret 없음)
     * 
     * Master realm의 admin 사용자로 로그인하면:
     * - Realm 생성 가능
     * - 생성된 realm에 자동으로 admin 권한 부여되어 client 생성 가능
     */
    private ClientRegistration createClientRegistration(String realm) {
        String issuer = authServerUrl + "/realms/" + realm;

        try {
            // Public client로 생성 (clientSecret 없음)
            // ClientAuthenticationMethod.NONE을 명시적으로 설정하여 PKCE 활성화
            return ClientRegistrations.fromIssuerLocation(issuer)
                    .registrationId(realm)
                    .clientId(defaultClientId)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)  // PKCE를 위해 명시적 설정
                    .scope("openid", "profile", "email", "roles", "offline_access")
                    .clientName("Keycloak (" + realm + ")")
                    .build();
        } catch (Exception e) {
            // In a real app, handle connection errors or invalid realms more gracefully
            throw new IllegalArgumentException("Failed to resolve realm metadata for: " + realm, e);
        }
    }
}
