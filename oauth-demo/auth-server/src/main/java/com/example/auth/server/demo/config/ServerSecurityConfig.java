package com.example.auth.server.demo.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static com.example.auth.server.demo.config.CustomClientMetadataConfig.configureCustomClientMetadataConverters;


@Configuration
@Import(OAuth2AuthorizationServerConfiguration.class)
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
    public SecurityFilterChain securityFilterChain(
            RegisteredClientRepository registeredClientRepository,
            OAuth2AuthorizationService authorizationService,
            OAuth2AuthorizationConsentService authorizationConsentService,
            OAuth2TokenGenerator<OAuth2Token> tokenGenerator,
            JwtEncoder jwtEncoder,
            JwtDecoder jwtDecoder,
            HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/connect/register").permitAll()
                        .requestMatchers("/oauth2/authorize").permitAll()
                        .requestMatchers("/oauth2/token").permitAll()
                )
                .formLogin(Customizer.withDefaults())
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
        ;

        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = http.getConfigurer(OAuth2AuthorizationServerConfigurer.class);
        authorizationServerConfigurer
                //.oidc(Customizer.withDefaults()) // Initialize `OidcConfigurer`
                .oidc(oidc -> oidc.clientRegistrationEndpoint(clientRegistrationEndpoint -> {
                    clientRegistrationEndpoint
                            .authenticationProviders(configureCustomClientMetadataConverters());
                }))
                .registeredClientRepository(registeredClientRepository)
                .authorizationService(authorizationService)
                .authorizationConsentService(authorizationConsentService)
                .tokenGenerator(tokenGenerator)
                .authorizationServerSettings(AuthorizationServerSettings.builder().build())
                /*.authorizationServerSettings( AuthorizationServerSettings.builder()
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
                )*/
        ;
        http.oauth2ResourceServer(configurer ->
                configurer.jwt(jwtConfigurer -> {
                    jwtConfigurer.decoder(jwtDecoder);
                })
        );
        return http.build();
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring() // 해당 경로는 보안 필터를 완전히 무시
                .requestMatchers("/error")
                .requestMatchers("/h2-console/**");
    }
}