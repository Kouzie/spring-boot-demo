package com.example.keycloak.adapter;

import com.example.keycloak.dto.KeycloakUserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class KeycloakAdapter {

    @Value("${keycloak.auth-server-url:http://localhost:8080}")
    private String authServerUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public KeycloakUserDto getUserInfo(String realm, String username, String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String url = authServerUrl + "/admin/realms/" + realm + "/users?username=" + username;
            ResponseEntity<List<KeycloakUserDto>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<KeycloakUserDto>>() {
                    });

            List<KeycloakUserDto> users = response.getBody();
            if (users != null && !users.isEmpty()) {
                KeycloakUserDto user = users.get(0);

                // Fetch role mappings
                String roleMappingsUrl = authServerUrl + "/admin/realms/" + realm + "/users/" + user.getId()
                        + "/role-mappings";
                ResponseEntity<java.util.Map> roleResponse = restTemplate.exchange(
                        roleMappingsUrl, HttpMethod.GET, entity, java.util.Map.class);

                java.util.Map<String, Object> mappings = roleResponse.getBody();
                if (mappings != null) {
                    if (mappings.containsKey("realmMappings")) {
                        List<java.util.Map<String, Object>> realmMappings = (List<java.util.Map<String, Object>>) mappings
                                .get("realmMappings");
                        java.util.List<String> realmRoles = new java.util.ArrayList<>();
                        for (java.util.Map<String, Object> mapping : realmMappings) {
                            realmRoles.add((String) mapping.get("name"));
                        }
                        user.setRealmRoles(realmRoles);
                    }
                    if (mappings.containsKey("clientMappings")) {
                        java.util.Map<String, java.util.Map<String, Object>> clientMappings = (java.util.Map<String, java.util.Map<String, Object>>) mappings
                                .get("clientMappings");
                        java.util.Map<String, Object> simpleClientRoles = new java.util.HashMap<>();
                        for (java.util.Map.Entry<String, java.util.Map<String, Object>> entry : clientMappings
                                .entrySet()) {
                            java.util.Map<String, Object> clientData = entry.getValue();
                            String clientName = (String) clientData.get("client");
                            List<java.util.Map<String, Object>> roleList = (List<java.util.Map<String, Object>>) clientData
                                    .get("mappings");
                            java.util.List<String> roles = new java.util.ArrayList<>();
                            for (java.util.Map<String, Object> roleMap : roleList) {
                                roles.add((String) roleMap.get("name"));
                            }
                            simpleClientRoles.put(clientName, roles);
                        }
                        user.setClientRoles(simpleClientRoles);
                    }
                }
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
