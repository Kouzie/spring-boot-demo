package com.example.auth.resourceclient.demo.config;

import com.example.auth.resourceclient.demo.client.KakaoOidc2UserService;
import com.example.auth.resourceclient.demo.client.NaverOAuth2UserService;
import com.example.auth.resourceclient.demo.client.SpringOidc2UserService;
import com.example.auth.resourceclient.demo.config.jwt.JwtAuthenticationFilter;
import com.example.auth.resourceclient.demo.config.jwt.JwtUtil;
import com.example.auth.resourceclient.demo.config.jwt.OAuthLoginSuccessHandler;
import com.example.auth.resourceclient.demo.model.ResourceClientUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DelegatingOAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;


@Configuration
public class ResourceClientConfig {

    @Value("${naver.oauth.client.id}")
    private String naverOAuthClientId;
    @Value("${naver.oauth.client.secret}")
    private String naverOAuthClientSecret;
    @Value("${kakao.oauth.client.id}")
    private String kakaoOAuthClientId;
    @Value("${kakao.oauth.client.secret}")
    private String kakaoOAuthClientSecret;

    // Resource Client 가 여러개 띄어져 있어도 JDBC 를 통해 DB 에서 access token, refresh token 등을 검색하기 때문에 로그인이 풀리지 않음
    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(DataSource dataSource,
                                                                 ClientRegistrationRepository clientRegistrationRepository) {
        return new JdbcOAuth2AuthorizedClientService(new JdbcTemplate(dataSource), clientRegistrationRepository);
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        // oidc 를 지원할 경우 /.well-known/openid-configuration URL 을 통해
        // auth code, token, userinfo 를 가져오는 url 을 자동으로 등록한다
        ClientRegistration springAuthDemoClient = ClientRegistrations.fromIssuerLocation("http://localhost:9090")
                .registrationId("oauth-demo-registration-id")
                .clientId("oauth-demo-client-id")
                .clientSecret("secret")
                .clientName("Resource Client Demo")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE) // PKCE 방식, code_challenge code_verifier 를 사용
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/oauth-client-redirect")
                .scope(OidcScopes.OPENID, OidcScopes.PROFILE, OidcScopes.EMAIL)
                .build();
        // naver 에선 oauth 2.0 만 지원, 각종 oauth 관련 url 을 수기로 작성해줘야 한다.
        ClientRegistration naverAuthClient = ClientRegistration.withRegistrationId("naver-auth-registration-id")
                .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
                .tokenUri("https://nid.naver.com/oauth2.0/token")
                .userInfoUri("https://openapi.naver.com/v1/nid/me")
                .clientId(naverOAuthClientId)
                .clientSecret(naverOAuthClientSecret)
                .clientName("Naver OAuth Client Demo")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC) // HTTP Basic 인증 헤더사용, Authorization 헤더에 client secret Base64
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/naver-oauth-redirect")
                .userNameAttributeName("response") // 응답값중 username 값을 가지고 있는 json key 값, naver 의 경우 response 로 한겹 더 감싸져있어 부득이하기 부모 key 값을 써야함
                .scope("name", "email")
                .build();

        // kakao 에선 oidc 를 지원한다.
        // https://kauth.kakao.com/.well-known/openid-configuration
        ClientRegistration kakaoAuthClient = ClientRegistrations.fromIssuerLocation("https://kauth.kakao.com")
                .registrationId("kakao-auth-registration-id")
                .clientId(kakaoOAuthClientId)
                .clientSecret(kakaoOAuthClientSecret)
                .clientName("Kakao OAuth Client Demo")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST) // Form 데이터 형태로 client secret Base64
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/kakao-auth-redirect")
                .userNameAttributeName(IdTokenClaimNames.SUB) // default 가 sub
                .scope(OidcScopes.OPENID) // https://developers.kakao.com/docs/latest/ko/kakaologin/utilize#scope-user
                .build();
        /* { resultcode=00, message=success, response={id=xqmroU.., name=홍길동, email=kouzie@naver.com} } */
        return new InMemoryClientRegistrationRepository(springAuthDemoClient, naverAuthClient, kakaoAuthClient);
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring() // 해당 경로는 보안 필터를 완전히 무시
                .requestMatchers("/")
                .requestMatchers("/login")
                .requestMatchers("/error")
                .requestMatchers("/h2-console/**");
    }

    public HttpSecurity setDefaultHttpSecurity(ClientRegistrationRepository clientRegistrationRepository,
                                                      OAuth2AuthorizedClientService oAuth2AuthorizedClientService,
                                                      ResourceClientUserService resourceClientUserService,
                                                      HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .clientRegistrationRepository(clientRegistrationRepository) // naver kakao spring oauth 설정
                        .authorizedClientService(oAuth2AuthorizedClientService) // jdbc authorizedClientService 사용하도록 변경
                        .defaultSuccessUrl("/main", false) // alwaysUse 는 이전 방문 페이지로 이동시킴
                        .loginPage("/login") // custom login page 지정
                        .userInfoEndpoint(userinfo -> userinfo // userinfo 요청 처리 객체
                                .userService(new DelegatingOAuth2UserService(List.of(
                                        new NaverOAuth2UserService(resourceClientUserService)
                                )))
                                .oidcUserService(new DelegatingOAuth2UserService(List.of(
                                        new KakaoOidc2UserService(resourceClientUserService),
                                        new SpringOidc2UserService()
                                )))
                        )
                );
        http.exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
        );
        return http;
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                response.sendRedirect("/login?error=UNAUTHORIZED");
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
    @Profile("session")
    public SecurityFilterChain securityFilterChainSession(ClientRegistrationRepository clientRegistrationRepository,
                                                   OAuth2AuthorizedClientService oAuth2AuthorizedClientService,
                                                   ResourceClientUserService resourceClientUserService,
                                                   HttpSecurity http) throws Exception {
        setDefaultHttpSecurity(clientRegistrationRepository,
                oAuth2AuthorizedClientService,
                resourceClientUserService,
                http);
        return http.build();
    }


    @Bean
    @Profile("jwt")
    public SecurityFilterChain securityFilterChainJwt(ClientRegistrationRepository clientRegistrationRepository,
                                                   OAuth2AuthorizedClientService oAuth2AuthorizedClientService,
                                                   ResourceClientUserService resourceClientUserService,
                                                   OAuthLoginSuccessHandler oAuthLoginSuccessHandler,
                                                   JwtUtil jwtUtil,
                                                   HttpSecurity http) throws Exception {
        setDefaultHttpSecurity(clientRegistrationRepository,
                oAuth2AuthorizedClientService,
                resourceClientUserService,
                http);
        http.oauth2Login(oauth2 -> oauth2.successHandler(oAuthLoginSuccessHandler)); // 로그인 성공시 jwt 토큰을 cookie 에 추가
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class); // cookie 에 저장된 jwt 를 확인하는 filter 추가
        return http.build();
    }

}