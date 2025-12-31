package com.example.keycloak.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class PageController {

    @GetMapping("/")
    public String index(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/profile";
        }
        return "redirect:/login";
    }
    
    /**
     * 로그인 페이지 - 모든 사용자 접근 가능
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    /**
     * Realm 관리 페이지 - master realm 사용자만 접근 가능
     */
    @GetMapping("/realms")
    public String realmsPage(@AuthenticationPrincipal OidcUser principal, Model model) {
        if (principal == null) {
            throw new org.springframework.security.access.AccessDeniedException("Authentication required");
        }
        
        if (!isMasterRealm(principal)) {
            throw new org.springframework.security.access.AccessDeniedException("Only master realm users can access this page");
        }
        
        model.addAttribute("username", principal.getPreferredUsername());
        model.addAttribute("hasAdminRole", true);
        
        return "realms";
    }

    @GetMapping("/board")
    public String boardPage(@AuthenticationPrincipal OidcUser principal, Model model) {
        if (principal != null) {
            model.addAttribute("username", principal.getPreferredUsername());
            model.addAttribute("oidcUser", principal);
            model.addAttribute("hasAdminRole", isMasterRealm(principal));
        }
        return "board";
    }

    /**
     * Profile 페이지를 렌더링하는 컨트롤러
     * 
     * 서버 사이드 렌더링:
     * - OIDC ID Token의 Claims를 서버에서 추출하여 HTML에 전달
     * - 기본 정보만 포함 (username, email, 표준 OIDC Claims)
     * - 사용자 권한(Roles) 정보 포함
     * 
     * 클라이언트 사이드 추가 조회:
     * - profile.html의 JavaScript가 /user/info API를 호출
     * - Access Token을 사용하여 Keycloak UserInfo Endpoint에서 상세 정보 조회
     * - 추가 정보: ID, Full Name, Email Verified 등
     */
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal OidcUser principal, Authentication authentication, Model model) {
        if (principal != null) {
            // OIDC ID Token에서 기본 정보 추출
            model.addAttribute("username", principal.getPreferredUsername());
            model.addAttribute("email", principal.getEmail());
            // 모든 Claims (Token에 포함된 모든 정보)
            model.addAttribute("claims", principal.getClaims());
            
            // 모든 권한 정보 추출 (realm roles, client roles 등)
            List<String> allAuthorities = new ArrayList<>();
            
            // 1. Realm Roles 추출 (realm_access.roles)
            Map<String, Object> claims = principal.getClaims();
            if (claims.containsKey("realm_access")) {
                Map<String, Object> realmAccess = (Map<String, Object>) claims.get("realm_access");
                if (realmAccess.containsKey("roles")) {
                    List<String> realmRoles = (List<String>) realmAccess.get("roles");
                    allAuthorities.addAll(realmRoles);
                    log.debug("Realm roles: {}", realmRoles);
                }
            }
            
            // 2. Client Roles 추출 (resource_access.{client-id}.roles)
            if (claims.containsKey("resource_access")) {
                Map<String, Object> resourceAccess = (Map<String, Object>) claims.get("resource_access");
                resourceAccess.forEach((clientId, clientAccess) -> {
                    if (clientAccess instanceof Map) {
                        Map<String, Object> clientMap = (Map<String, Object>) clientAccess;
                        if (clientMap.containsKey("roles")) {
                            List<String> clientRoles = (List<String>) clientMap.get("roles");
                            // Client ID를 prefix로 추가하여 구분
                            clientRoles.forEach(role -> allAuthorities.add(clientId + ":" + role));
                            log.debug("Client roles for {}: {}", clientId, clientRoles);
                        }
                    }
                });
            }
            
            // 3. Spring Security Authorities에서 ROLE_ prefix가 있는 권한도 추가
            List<String> springAuthorities = authentication.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .filter(role -> role.startsWith("ROLE_"))
                    .map(role -> role.substring(5)) // "ROLE_" prefix 제거
                    .collect(Collectors.toList());
            
            // 중복 제거하면서 추가
            springAuthorities.forEach(auth -> {
                if (!allAuthorities.contains(auth)) {
                    allAuthorities.add(auth);
                }
            });
            
            log.info("User {} has {} total authorities: {}", principal.getPreferredUsername(), allAuthorities.size(), allAuthorities);
            model.addAttribute("authorities", allAuthorities);
            // Scope 정보 추출
            // 1. ID Token의 scope claim 확인
            List<String> scopes = new ArrayList<>();
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauth2AuthToken = (OAuth2AuthenticationToken) authentication;
                if (oauth2AuthToken.getAuthorities() != null && !oauth2AuthToken.getAuthorities().isEmpty()) {
                    for (GrantedAuthority authority : oauth2AuthToken.getAuthorities()) {
                        scopes.add(authority.getAuthority());
                    }
                }
            }
            model.addAttribute("scopes", scopes);
            model.addAttribute("hasAdminRole", isMasterRealm(principal));
        }
        return "profile";
    }

    private boolean isMasterRealm(OidcUser principal) {
        String issuer = principal.getIssuer() != null ? principal.getIssuer().toString() : "";
        return issuer.contains("/realms/master");
    }
}
