package com.example.keycloak.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakAdapter {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    // Admin access token 캐시
    private String cachedAdminAccessToken;
    private long tokenExpiresAt; // 토큰 만료 시간 (밀리초)

    /**
     * OIDC UserInfo Endpoint를 사용하여 사용자 정보 조회
     * 
     * @param realm       조회할 Keycloak Realm 이름
     * @param accessToken 사용자의 Access Token (Bearer Token으로 사용)
     *                    - 이 토큰은 사용자가 로그인할 때 발급받은 것
     *                    - 사용자마다 다른 토큰이 전달됨
     *                    - 특별한 권한 없이도 자신의 정보 조회 가능 (표준 OIDC)
     * @return 사용자 정보 (sub, email, preferred_username, name 등 기본 OIDC Claims)
     */
    public Map<String, Object> getUserInfo(String realm, String accessToken) {
        String url = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/userinfo";
        log.debug("Requesting user info from: {}", url);

        try {
            Map<String, Object> userInfo = restClient.get()
                    .uri(url)
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (request, response) -> {
                        log.error("UserInfo endpoint error: {} {}", response.getStatusCode(), response.getStatusText());
                        throw new HttpClientErrorException(response.getStatusCode(),
                                "Failed to get user info: " + response.getStatusText());
                    })
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            return userInfo;
        } catch (HttpClientErrorException e) {
            log.error("Failed to get user info from Keycloak. Status: {}, Response: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while getting user info: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get user info", e);
        }
    }

    /**
     * JWT 토큰에서 exp claim 추출 (서명 검증 없이 payload만 디코딩)
     * 
     * @param token JWT 토큰
     * @return exp claim 값 (초 단위 Unix timestamp), 없으면 -1
     */
    private long extractExpFromToken(String token) {
        try {
            // JWT는 Header.Payload.Signature 형식
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return -1;
            }

            // Payload 디코딩 (Base64 URL-safe)
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            Map<String, Object> claims = objectMapper.readValue(payload,
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
                    });

            Object expObj = claims.get("exp");
            if (expObj != null) {
                // exp는 초 단위 Unix timestamp이므로 밀리초로 변환
                long expSeconds = Long.parseLong(expObj.toString());
                return expSeconds * 1000;
            }
        } catch (Exception e) {
            log.debug("Failed to extract exp from token: {}", e.getMessage());
        }
        return -1;
    }

    /**
     * admin-cli를 사용하여 admin 계정의 access token 획득
     * realm 신규 생성시 해당 realm 의 CRUD 는 admin-cli access token 으로만 가능
     * 나머지 access token 은 로그아웃 했다 재로그인해야 권한 refresh
     * 
     * 토큰 캐싱: 유효기간이 남아있으면 캐시된 토큰 반환, 만료되었으면 새로 발급
     * - JWT 토큰의 exp claim을 우선 사용
     * - 응답의 expires_in을 보조로 사용
     * 
     * @return admin access token
     */
    public synchronized String getAdminCliAccessToken() {
        long now = System.currentTimeMillis();

        // 캐시된 토큰이 있으면 JWT의 exp claim으로 만료 시간 확인
        if (cachedAdminAccessToken != null) {
            long tokenExpFromJwt = extractExpFromToken(cachedAdminAccessToken);

            if (tokenExpFromJwt > 0) {
                // JWT의 exp claim 사용 (만료 30초 전까지 유효)
                if (now < (tokenExpFromJwt - 30000)) {
                    log.debug("Using cached admin access token (expires at {}, {} seconds remaining)",
                            tokenExpFromJwt, (tokenExpFromJwt - now) / 1000);
                    return cachedAdminAccessToken;
                } else {
                    log.debug("Cached token expired (exp: {}, now: {})", tokenExpFromJwt, now);
                }
            } else if (now < (tokenExpiresAt - 30000)) {
                // JWT exp를 읽을 수 없으면 캐시된 만료 시간 사용
                log.debug("Using cached admin access token (expires in {} seconds, JWT exp not available)",
                        (tokenExpiresAt - now) / 1000);
                return cachedAdminAccessToken;
            }
        }

        // 토큰이 없거나 만료되었으면 새로 발급
        String tokenUrl = authServerUrl + "/realms/master/protocol/openid-connect/token";
        log.debug("Requesting new admin access token from: {}", tokenUrl);

        Map<String, String> formData = new HashMap<>();
        formData.put("grant_type", "password");
        formData.put("client_id", "admin-cli");
        formData.put("username", adminUsername);
        formData.put("password", adminPassword);

        try {
            String formBody = formData.entrySet().stream()
                    .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));

            Map<String, Object> tokenResponse = restClient.post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formBody)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (request, response) -> {
                        log.error("Failed to get admin access token: {} {}", response.getStatusCode(),
                                response.getStatusText());
                        throw new HttpClientErrorException(response.getStatusCode(),
                                "Failed to get admin access token: " + response.getStatusText());
                    })
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            if (tokenResponse == null) {
                throw new RuntimeException("Token response is null");
            }

            String accessToken = (String) tokenResponse.get("access_token");
            if (accessToken == null) {
                throw new RuntimeException("Access token not found in response");
            }

            // 토큰 만료 시간 계산 (JWT의 exp claim 우선, 없으면 expires_in 사용)
            long tokenExp = extractExpFromToken(accessToken);

            if (tokenExp > 0) {
                // JWT의 exp claim 사용 (밀리초)
                tokenExpiresAt = tokenExp;
                log.debug("Token expiration from JWT exp claim: {} ({} seconds from now)",
                        tokenExp, (tokenExp - now) / 1000);
            } else {
                // JWT exp를 읽을 수 없으면 응답의 expires_in 사용
                Object expiresInObj = tokenResponse.get("expires_in");
                long expiresIn = expiresInObj != null ? Long.parseLong(expiresInObj.toString()) * 1000 : 300000; // 기본
                // 5분
                // (밀리초)
                tokenExpiresAt = now + expiresIn;
                log.debug("Token expiration from expires_in: {} ({} seconds)",
                        tokenExpiresAt, expiresIn / 1000);
            }

            // 응답의 다른 만료 관련 필드 로깅 (디버깅용)
            if (log.isDebugEnabled()) {
                log.debug("Token response fields: expires_in={}, refresh_expires_in={}, token_type={}",
                        tokenResponse.get("expires_in"),
                        tokenResponse.get("refresh_expires_in"),
                        tokenResponse.get("token_type"));
            }

            cachedAdminAccessToken = accessToken;

            log.debug("Successfully obtained and cached admin access token (expires at {}, {} seconds remaining)",
                    tokenExpiresAt, (tokenExpiresAt - now) / 1000);
            return accessToken;
        } catch (HttpClientErrorException e) {
            // 에러 발생 시 캐시 초기화
            cachedAdminAccessToken = null;
            tokenExpiresAt = 0;
            log.error("Failed to get admin access token. Status: {}, Response: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to get admin access token", e);
        } catch (Exception e) {
            // 에러 발생 시 캐시 초기화
            cachedAdminAccessToken = null;
            tokenExpiresAt = 0;
            log.error("Unexpected error while getting admin access token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get admin access token", e);
        }
    }

    /**
     * realm 목록을 조회
     */
    public List<Map<String, Object>> adminApiGetRealmList(String accessToken) {
        String url = authServerUrl + "/admin/realms";
        try {
            List<Map<String, Object>> realms = restClient.get()
                    .uri(url)
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (request, response) -> {
                        log.error("Keycloak Admin API error: {} {}", response.getStatusCode(),
                                response.getStatusText());
                        throw new HttpClientErrorException(response.getStatusCode(),
                                "Failed to get realm list: " + response.getStatusText());
                    })
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });

            if (realms != null) {
                log.info("Fetched {} realms from Keycloak", realms.size());
            } else {
                log.warn("Keycloak Admin API returned null realm list");
            }

            return realms;
        } catch (HttpClientErrorException e) {
            log.error("Failed to get realm list from Keycloak. Status: {}, Response: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while getting realm list: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get realm list", e);
        }
    }

    /**
     * Keycloak Admin API - Realm 생성
     */
    public void adminApiCreateRealm(String accessToken, String realmName, boolean enabled) {
        String realmUrl = authServerUrl + "/admin/realms";
        Map<String, Object> realm = new HashMap<>();
        realm.put("realm", realmName);
        realm.put("enabled", enabled);

        try {
            var response = restClient.post()
                    .uri(realmUrl)
                    .headers(h -> h.setBearerAuth(accessToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(realm)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Realm '{}' created successfully. Response status: {}", realmName, response.getStatusCode());
        } catch (HttpClientErrorException e) {
            log.error("Failed to create realm '{}': Status {}, Response: {}",
                    realmName, e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    /**
     * Keycloak Admin API - Realm 삭제
     */
    public void adminApiDeleteRealm(String accessToken, String realmName) {
        String url = authServerUrl + "/admin/realms/" + realmName;
        restClient.delete()
                .uri(url)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .toBodilessEntity();
        log.info("Realm '{}' deleted", realmName);
    }

    /**
     * Keycloak Admin API - Client 존재 여부 확인
     */
    public boolean adminApiClientExists(String accessToken, String realmName, String clientId) {
        String url = authServerUrl + "/admin/realms/" + realmName + "/clients?clientId=" + clientId;
        try {
            List<Map<String, Object>> clients = restClient.get()
                    .uri(url)
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });

            return clients != null && !clients.isEmpty();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                return false;
            }
            log.warn("Failed to check if client '{}' exists in realm '{}': {}", clientId, realmName, e.getMessage());
            return false;
        } catch (Exception e) {
            log.warn("Unexpected error while checking client existence: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Keycloak Admin API - Protocol Mapper 추가
     * 역할 정보를 claims에 포함시키기 위한 Mapper 추가
     * Clients > Client details > Dedicated scopes 에서 확인 가능
     */
    private void addProtocolMappers(String accessToken, String realmName, String clientId) {
        try {
            // Client UUID 조회
            String clientUrl = authServerUrl + "/admin/realms/" + realmName + "/clients?clientId=" + clientId;
            List<Map<String, Object>> clients = restClient.get()
                    .uri(clientUrl)
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });

            if (clients == null || clients.isEmpty()) {
                log.warn("Could not find client '{}' in realm '{}', skipping protocol mapper addition",
                        clientId, realmName);
                return;
            }

            Object id = clients.get(0).get("id");
            if (id == null) {
                log.warn("Client '{}' has no UUID in realm '{}', skipping protocol mapper addition",
                        clientId, realmName);
                return;
            }

            String clientUuid = id.toString();

            String mapperUrl = authServerUrl + "/admin/realms/" + realmName + "/clients/" + clientUuid + "/protocol-mappers/models";

            // 1. Realm Roles Mapper 추가 (realm_access.roles)
            Map<String, Object> realmRolesMapper = new HashMap<>();
            realmRolesMapper.put("name", "realm-roles");
            realmRolesMapper.put("protocol", "openid-connect");
            realmRolesMapper.put("protocolMapper", "oidc-usermodel-realm-role-mapper");
            Map<String, String> realmMapperConfig = new HashMap<>();
            realmMapperConfig.put("user.attribute", "");
            realmMapperConfig.put("claim.name", "realm_access.roles");
            realmMapperConfig.put("jsonType.label", "String");
            realmMapperConfig.put("id.token.claim", "true");
            realmMapperConfig.put("access.token.claim", "true");
            realmMapperConfig.put("userinfo.token.claim", "true");
            realmMapperConfig.put("multivalued", "true");
            realmMapperConfig.put("aggregate.attrs", "false");
            realmRolesMapper.put("config", realmMapperConfig);

            restClient.post()
                    .uri(mapperUrl)
                    .headers(h -> h.setBearerAuth(accessToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(realmRolesMapper)
                    .retrieve()
                    .toBodilessEntity();
            log.debug("Realm roles mapper added to client '{}' in realm '{}'", clientId, realmName);

            // 2. Client Roles Mapper 추가 (resource_access.{client-id}.roles)
            Map<String, Object> clientRolesMapper = new HashMap<>();
            clientRolesMapper.put("name", "client-roles");
            clientRolesMapper.put("protocol", "openid-connect");
            clientRolesMapper.put("protocolMapper", "oidc-usermodel-client-role-mapper");
            Map<String, String> clientMapperConfig = new HashMap<>();
            clientMapperConfig.put("user.attribute", "");
            clientMapperConfig.put("claim.name", "resource_access.${client_id}.roles");
            clientMapperConfig.put("jsonType.label", "String");
            clientMapperConfig.put("id.token.claim", "true");
            clientMapperConfig.put("access.token.claim", "true");
            clientMapperConfig.put("userinfo.token.claim", "true");
            clientMapperConfig.put("multivalued", "true");
            clientMapperConfig.put("aggregate.attrs", "false");
            clientRolesMapper.put("config", clientMapperConfig);

            restClient.post()
                    .uri(mapperUrl)
                    .headers(h -> h.setBearerAuth(accessToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(clientRolesMapper)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Protocol mappers (realm-roles, client-roles) added successfully to client '{}' in realm '{}'", clientId, realmName);
        } catch (Exception e) {
            log.debug("Failed to get client UUID for '{}' in realm '{}': {}", clientId, realmName, e.getMessage());
        }
    }

    /**
     * PKCE를 지원하는 public client로 생성
     */
    public void adminApiCreateClient(String accessToken, String realmName, String clientId) {
        String clientUrl = authServerUrl + "/admin/realms/" + realmName + "/clients";

        Map<String, Object> client = new HashMap<>();
        client.put("clientId", clientId);
        client.put("name", "Demo Client");
        client.put("enabled", true);
        client.put("publicClient", true);
        client.put("standardFlowEnabled", true);
        client.put("directAccessGrantsEnabled", false); // PKCE 사용 시 false
        client.put("serviceAccountsEnabled", false);

        // Redirect URIs 및 Web Origins 설정
        client.put("redirectUris", List.of("http://localhost:8081/*"));
        client.put("webOrigins", List.of("http://localhost:8081"));

        // PKCE 설정
        Map<String, String> attributes = new HashMap<>();
        attributes.put("pkce.code.challenge.method", "S256");
        client.put("attributes", attributes);

        try {
            ResponseEntity<Void> response = restClient.post()
                    .uri(clientUrl)
                    .headers(h -> h.setBearerAuth(accessToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(client)
                    .retrieve()
                    .toBodilessEntity();
            addProtocolMappers(accessToken, realmName, clientId);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 403) {
                log.warn("Insufficient permissions to create client '{}' in realm '{}'", clientId, realmName);
            } else {
                log.error("Failed to create client '{}' in realm '{}': {}", clientId, realmName, e.getMessage());
            }
        } catch (Exception e) {
            log.error("Unexpected error creating client '{}' in realm '{}': {}", clientId, realmName, e.getMessage());
        }
    }
}
