package com.example.keycloak.controller;

import com.example.keycloak.adapter.KeycloakAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/keycloak")
@RequiredArgsConstructor
public class AdminController {

    @Value("${keycloak.client-id}")
    private String defaultClientId;

    private final KeycloakAdapter keycloakAdapter;
    private final OAuth2AuthorizedClientService authorizedClientService;
    /**
     * Admin Access Token 테스트 API
     */
    @GetMapping("/test/token")
    public ResponseEntity<Map<String, Object>> testAdminToken() {
        Map<String, Object> result = new HashMap<>();
        try {
            String accessToken = keycloakAdapter.getAdminCliAccessToken();
            result.put("status", "success");
            result.put("tokenLength", accessToken != null ? accessToken.length() : 0);
            result.put("tokenPrefix", accessToken != null && accessToken.length() > 20 ? 
                    accessToken.substring(0, 20) + "..." : "N/A");
            result.put("message", "Admin access token retrieved successfully");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * Realm 목록 조회 API
     */
    @GetMapping("/realms")
    public ResponseEntity<List<Map<String, Object>>> getRealms() {
        try {
            String accessToken = keycloakAdapter.getAdminCliAccessToken();
            List<Map<String, Object>> realms = keycloakAdapter.adminApiGetRealmList(accessToken);
            return ResponseEntity.ok(realms);
        } catch (HttpClientErrorException.Forbidden e) {
            log.warn("Access denied: User does not have permission to view realms");
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            log.error("Failed to get realms", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Realm 삭제 API
     */
    @DeleteMapping("/realm/{realmName}")
    public ResponseEntity<Map<String, Object>> deleteRealm(@PathVariable String realmName, Authentication authentication) {
        try {
            String accessToken = getAccessToken(authentication);
            keycloakAdapter.adminApiDeleteRealm(accessToken, realmName);
            
            return ResponseEntity.ok(createSuccessResponse("Realm '" + realmName + "' deleted successfully", realmName));
        } catch (HttpClientErrorException.Forbidden e) {
            log.warn("Access denied: User does not have permission to delete realm '{}'", realmName);
            return ResponseEntity.status(403).body(createErrorResponse("Access denied: Insufficient permissions"));
        } catch (Exception e) {
            log.error("Realm deletion failed", e);
            return ResponseEntity.status(500).body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Realm 생성 API
     */
    @PostMapping("/realm")
    public ResponseEntity<Map<String, Object>> createRealm(@RequestBody Map<String, String> request, Authentication authentication) {
        String realmName = request.get("realm");

        if (realmName == null || realmName.isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorResponse("Realm name is required"));
        }

        try {
            String accessToken = keycloakAdapter.getAdminCliAccessToken();
            keycloakAdapter.adminApiCreateRealm(accessToken, realmName, true);
            keycloakAdapter.adminApiCreateClient(accessToken, realmName, defaultClientId);

            return ResponseEntity.ok(createSuccessResponse("Realm '" + realmName + "' created successfully", realmName));
        } catch (HttpClientErrorException.Forbidden e) {
            log.warn("Access denied: User does not have permission to create realm");
            return ResponseEntity.status(403).body(createErrorResponse("Access denied: Insufficient permissions"));
        } catch (Exception e) {
            log.error("Realm creation failed", e);
            return ResponseEntity.status(500).body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Client 생성 API
     */
    @PostMapping("/realm/{realmName}/client")
    public ResponseEntity<Map<String, Object>> createClient(
            @PathVariable String realmName,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        String clientId = request.get("clientId");

        if (clientId == null || clientId.isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorResponse("Client ID is required"));
        }

        try {
            String accessToken = getAccessToken(authentication);
            keycloakAdapter.adminApiCreateClient(accessToken, realmName, clientId);

            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("message", "Client '" + clientId + "' created successfully in realm '" + realmName + "'");
            result.put("realm", realmName);
            result.put("clientId", clientId);

            return ResponseEntity.ok(result);
        } catch (HttpClientErrorException.Forbidden e) {
            log.warn("Access denied: User does not have permission to create client in realm '{}'", realmName);
            return ResponseEntity.status(403).body(createErrorResponse("Access denied: Insufficient permissions"));
        } catch (Exception e) {
            log.error("Client creation failed in realm '{}'", realmName, e);
            return ResponseEntity.status(500).body(createErrorResponse(e.getMessage()));
        }
    }

    private Map<String, Object> createSuccessResponse(String message, String realmName) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", message);
        result.put("realm", realmName);
        return result;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "error");
        result.put("message", message);
        return result;
    }

    /**
     * Authentication 객체에서 access token 추출
     * refresh token 자동 갱신을 우회하여 직접 token을 가져옴
     */
    private String getAccessToken(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    oauth2Token.getAuthorizedClientRegistrationId(),
                    oauth2Token.getName());
            if (client != null) {
                return client.getAccessToken().getTokenValue();
            }
        }
        throw new IllegalStateException("Unable to retrieve access token");
    }
}
