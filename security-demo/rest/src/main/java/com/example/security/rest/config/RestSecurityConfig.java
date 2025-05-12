package com.example.security.rest.config;

import com.example.security.common.config.MemberRole;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
public class RestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // Form Login 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 관리 비활성화
                // SecurityContextPersistenceFilter 에서 HttpSession 에 SecurityContext 를 저장하는것을 명시적으로 제거
                // 다른 SecurityFilter 에서 HttpSession 객체에 접근하는것을 명시적으로 제거
                .authorizeHttpRequests(auths -> auths
                        .requestMatchers("/boards/random").hasAnyRole(
                                MemberRole.ADMIN.name(),
                                MemberRole.MANAGER.name()
                        )
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                //.addFilterBefore(new JwtFilter(List.of("/login")), UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가
                .securityContext(context -> context
                        .securityContextRepository(new JwtSecurityContextRepository(List.of("/login")))
                        .requireExplicitSave(true) // default true, saveContext 생략처리됨
                )
        ;
        return http.build();
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        };
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        };
    }

    @Bean
    public MethodSecurityExpressionHandler createExpressionHandler(PermissionEvaluator customPermissionEvaluator) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(customPermissionEvaluator);
        return expressionHandler;
    }
}