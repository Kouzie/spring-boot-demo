package com.example.auth.server.demo.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import static com.example.auth.server.demo.config.CustomClientMetadataConfig.configureCustomClientMetadataConverters;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;


@Configuration
@RequiredArgsConstructor
public class ServerSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }


    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public OAuth2TokenCustomizer<OAuth2TokenClaimsContext> accessTokenCustomizer() {
        return context -> {
            OAuth2TokenClaimsSet.Builder claims = context.getClaims();
            // Customize claims

        };
    }

    @Bean
    public OAuth2TokenGenerator<OAuth2Token> tokenGenerator(JwtEncoder jwtEncoder,
                                                            OAuth2TokenCustomizer<OAuth2TokenClaimsContext> accessTokenCustomizer) {
        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
        OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
        accessTokenGenerator.setAccessTokenCustomizer(accessTokenCustomizer);
        OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
        return new DelegatingOAuth2TokenGenerator(
                jwtGenerator, // id_token
                accessTokenGenerator, // access_token
                refreshTokenGenerator // refresh_token
        );
    }

    // oauth core module 등록
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(RegisteredClientRepository registeredClientRepository,
                                                                      OAuth2AuthorizationService authorizationService,
                                                                      OAuth2AuthorizationConsentService authorizationConsentService,
                                                                      OAuth2TokenGenerator<OAuth2Token> tokenGenerator,
                                                                      JwtEncoder jwtEncoder,
                                                                      JwtDecoder jwtDecoder,
                                                                      HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        OAuth2AuthorizationServerConfigurer authz = http.getConfigurer(OAuth2AuthorizationServerConfigurer.class);
        authz
                .registeredClientRepository(registeredClientRepository)
                .authorizationService(authorizationService)
                .authorizationConsentService(authorizationConsentService)
                .tokenGenerator(tokenGenerator)
                .authorizationServerSettings(AuthorizationServerSettings.builder().build())
                .oidc(oidc -> oidc.clientRegistrationEndpoint(clientRegistrationEndpoint -> {
                    clientRegistrationEndpoint
                            .authenticationProviders(configureCustomClientMetadataConverters());
                }));
        /*authz
                .authorizationServerSettings( AuthorizationServerSettings.builder()
                        .issuer("http://localhost:8080")
                        .authorizationEndpoint("/oauth2/v1/authorize")
                        .deviceAuthorizationEndpoint("/oauth2/v1/device_authorization")
                        .deviceVerificationEndpoint("/oauth2/v1/device_verification")
                        .tokenEndpoint("/oauth2/v1/token")
                        .tokenIntrospectionEndpoint("/oauth2/v1/introspect")
                        .tokenRevocationEndpoint("/oauth2/v1/revoke")
                        .jwkSetEndpoint("/oauth2/v1/jwks")
                        .oidcLogoutEndpoint("/connect/v1/logout")
                        .oidcUserInfoEndpoint("/connect/v1/userinfo")
                        .oidcClientRegistrationEndpoint("/connect/v1/register")
                        .build())
                // 각 endpoint 에서 수행할 커스텀 동작들을 지정할 수 있다.
                .clientAuthentication(clientAuthentication -> { })
                .authorizationEndpoint(authorizationEndpoint -> { })
                .deviceAuthorizationEndpoint(deviceAuthorizationEndpoint -> { })
                .deviceVerificationEndpoint(deviceVerificationEndpoint -> { })
                .tokenEndpoint(tokenEndpoint -> { })
                .tokenIntrospectionEndpoint(tokenIntrospectionEndpoint -> { })
                .tokenRevocationEndpoint(tokenRevocationEndpoint -> { })
                .authorizationServerMetadataEndpoint(authorizationServerMetadataEndpoint -> { })
                .oidc(oidc -> oidc
                        .providerConfigurationEndpoint(providerConfigurationEndpoint -> { })
                        .logoutEndpoint(logoutEndpoint -> { })
                        .userInfoEndpoint(userInfoEndpoint -> { })
                        .clientRegistrationEndpoint(clientRegistrationEndpoint -> { })
                );*/
        http
                .securityMatchers(matchers -> matchers.requestMatchers(antMatcher("/oauth2/**"), authz.getEndpointsMatcher()))
                // Accept access tokens for User Info and/or Client Registration
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder)))
                // Redirect to the login page when not authenticated from the
                // authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))
        ;

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/login", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                // Form login handles the redirect to the login page from the
                // authorization server filter chain
                .formLogin(login -> login.loginPage("/login"))
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring() // 해당 경로는 보안 필터를 완전히 무시
                .requestMatchers("/error")
                .requestMatchers("/h2-console/**");
    }
}