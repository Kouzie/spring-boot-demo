package com.example.security.session.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
@Configuration
@EnableWebSecurity
public class SessionSecurityConfig {

    /*
    @Autowired
    DataSource datasource;

    private PersistentTokenRepository getJDBCRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(datasource);
        return jdbcTokenRepository;
    }
    */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   UserDetailsService userDetailsService) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
                .authorizeHttpRequests(auths -> auths
                        .requestMatchers("/boards/random").hasAnyRole("BASIC", "MANAGER")
                        .anyRequest().authenticated()
                )
                // login config
                .formLogin(formLogin -> formLogin
                        .usernameParameter("username_demo") // default: username
                        .passwordParameter("password_demo") // default: password
                        .loginPage("/auth/login_demo") // default: /login[GET]
                        .loginProcessingUrl("/auth/login_demo_process") // default: /login[POST]
                        //.successForwardUrl("/auth/login_success") // login success redirect url
                        .successHandler(new CustomLoginSuccessHandler("/boards/list"))
                        .failureUrl("/auth/login_demo?error=true") // login failed redirect url
                )
                // logout config
                .logout(logout -> logout
                        .logoutUrl("/auth/logout_demo") // default: /logout[GET, POST]
                        .logoutSuccessUrl("/boards/list") // logout success redirect url
                        .invalidateHttpSession(true) // logout 후 세션삭제여부, default: true
                )
                // exception config
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/auth/access_denied") // access denied redirect url
                )
                // remember me 설정
                .rememberMe(rememberMe -> rememberMe
                        .rememberMeParameter("remember-me") // default: remember-me
                        .key("spring-demo-security-key")
                        .tokenValiditySeconds(60 * 60 * 24) // 24 hour, default  2week
                        .alwaysRemember(false) // default: false
                        //.tokenRepository(getJDBCRepository()) // use PersistentTokenBasedRememberMeServices
                        .userDetailsService(userDetailsService) // use TokenBasedRememberMeServices
                )
        ;
        return http.build();
    }

    public static class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
        public CustomLoginSuccessHandler(String defaultTargetUrl) {
            setDefaultTargetUrl(defaultTargetUrl);
        }

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
            HttpSession session = request.getSession();
            if (session != null) {
                String redirectUrl = (String) session.getAttribute("prevPage");
                if (redirectUrl != null) {
                    session.removeAttribute("prevPage");
                    getRedirectStrategy().sendRedirect(request, response, redirectUrl);
                } else {
                    super.onAuthenticationSuccess(request, response, authentication);
                }
            } else {
                super.onAuthenticationSuccess(request, response, authentication);
            }
        }
    }
}