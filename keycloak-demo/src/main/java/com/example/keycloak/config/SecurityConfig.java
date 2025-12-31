package com.example.keycloak.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // @PreAuthorize, @PostAuthorize 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;

    /**
     * Spring Security 설정
     * - URL 레벨: 기본 접근 제어 (인증 여부만 체크)
     * - Controller 레벨: 세부 권한 체크 (create_post, delete_any_post, delete_own_post 등)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                        // 정적 리소스 및 로그인 페이지: 모두 허용
                        .requestMatchers("/css/**", "/js/**", "/error", "/", "/login")
                        .permitAll()
                        // 테스트 엔드포인트: 모두 허용
                        .requestMatchers("/admin/keycloak/test/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.disable())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/profile", true)
                        .userInfoEndpoint(userInfo -> userInfo.userAuthoritiesMapper(userAuthoritiesMapper())))
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            if (request.getRequestURI().equals("/realms"))
                                response.sendRedirect("/profile?error=access_denied");
                            else response.sendError(403);
                        }))
                .logout(logout -> logout
                        .logoutSuccessHandler(oidcLogoutSuccessHandler())
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"));
        return http.build();
    }

    /**
     * OIDC 로그아웃 핸들러
     * - Keycloak 로그아웃 엔드포인트를 호출하여 Keycloak 세션도 종료
     */
    @Bean
    public LogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler handler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        handler.setPostLogoutRedirectUri("{baseUrl}/login");
        return handler;
    }

    /**
     * Keycloak의 Roles를 Spring Security의 GrantedAuthority로 매핑
     * - Keycloak Group에 할당된 Role들이 realm_access.roles에 포함됨
     * - 예: delete_any_post, delete_own_post, create_post, view_post
     * - Spring Security에서는 "ROLE_" prefix를 붙여서 사용 (ROLE_delete_any_post 등)
     */
    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {
                if (authority instanceof OidcUserAuthority) {
                    OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
                    Map<String, Object> userInfo = oidcUserAuthority.getUserInfo().getClaims();

                    // realm_access.roles에서 권한 추출
                    if (userInfo.containsKey("realm_access")) {
                        Map<String, Object> realmAccess = (Map<String, Object>) userInfo.get("realm_access");
                        if (realmAccess.containsKey("roles")) {
                            List<String> roles = (List<String>) realmAccess.get("roles");
                            roles.forEach(role -> mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
                        }
                    }
                }
                mappedAuthorities.add(authority);
            });
            log.info("Total mapped authorities: {}", mappedAuthorities.size());
            return mappedAuthorities;
        };
    }
}
