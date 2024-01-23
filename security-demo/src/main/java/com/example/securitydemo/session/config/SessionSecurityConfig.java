package com.example.securitydemo.session.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Profile("session")
@Slf4j
@EnableWebSecurity
public class SessionSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/boards/random").hasAnyRole("BASIC", "MANAGER")
                .antMatchers("/boards/list").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin() // login config
                .usernameParameter("username_demo") // default: username
                .passwordParameter("password_demo") // default: password
                .loginPage("/auth/login_demo") // default: /login[GET]
                .loginProcessingUrl("/auth/login_demo_process") // default: /login[POST]
//                .successForwardUrl("/auth/login_success") // login success redirect url
                .successHandler(new CustomLoginSuccessHandler("/boards/list"))
                .failureUrl("/auth/login_demo?error=true") // login failed redirect url
                .and()
                .logout() // logout config
                .logoutUrl("/auth/logout_demo") // default: /logout[GET, POST]
                .logoutSuccessUrl("/boards/list") // logout success redirect url
                .invalidateHttpSession(true) // logout 후 세션삭제여부, default: true
                .and()
                .exceptionHandling() // exception config
                .accessDeniedPage("/auth/access_denied") // access denied redirect url
        ;
//                .authenticationEntryPoint(authenticationEntryPoint()) // 인증 예외
//                .accessDeniedHandler(accessDeniedHandler()) // 인가 예외
//                .defaultSuccessUrl("/boards/list")
        http.rememberMe()
                .rememberMeParameter("remember-me") // default: remember-me
                .key("spring-demo-security-key")
                .tokenValiditySeconds(60 * 60 * 24) // 24 hour, default  2week
                .alwaysRemember(false) // default: false
//                .tokenRepository(getJDBCRepository()) // use PersistentTokenBasedRememberMeServices
                .userDetailsService(userDetailsService) // use TokenBasedRememberMeServices
        ;
        return http.build();
    }

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
    public WebSecurityCustomizer webSecurityCustomizer() {
        return new WebSecurityCustomizer() {
            @Override
            public void customize(WebSecurity web) {
                web.ignoring()
                        .antMatchers("/auth/login_demo")
                        .antMatchers("/error")
                        .antMatchers("/h2-console/**");
            }
        };
    }

    /*
    private AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                log.error("url {} authentication denied, msg:{}", request.getRequestURL(), authException.getMessage());
                response.setStatus(401);
                response.sendRedirect("/login_demo");
            }
        };
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                log.warn("url {} access denied, msg:{}", request.getRequestURL(), accessDeniedException.getMessage());
                response.setStatus(403);
            }
        };
    }
    */


    /*
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(User.withUsername("user").password(passwordEncoder.encode("user")).roles("BASIC"))
                .withUser(User.withUsername("admin").password(passwordEncoder.encode("admin")).roles("BASIC", "ADMIN"));
    }
    */

    /*
    @Autowired
    private DataSource datasource;

    @Autowired
    private MemberRepository memberRepository;

    @PostConstruct
    private void init() {
        if (memberRepository.findByUname("basic").isEmpty()) {
            memberRepository.save(new Member("basic", passwordEncoder.encode("basic"), "BASIC"));
        }
        if (memberRepository.findByUname("manager").isEmpty()) {
            memberRepository.save(new Member("manager", passwordEncoder.encode("manager"), "MANAGER"));
        }
        if (memberRepository.findByUname("admin").isEmpty()) {
            memberRepository.save(new Member("admin", passwordEncoder.encode("admin"), "ADMIN"));
        }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //enable 은 해당 계정 사용가능 여부
        String query1 = "SELECT uid username, upw password, true enabled FROM tbl_members WHERE uname = ?";
        String query2 = "SELECT uid, role_name role FROM tbl_member_role WHERE uid = ?";
        auth.jdbcAuthentication()
                .dataSource(datasource)
                .usersByUsernameQuery(query1)
                .authoritiesByUsernameQuery(query2)
                .rolePrefix("ROLE_");
    }*/

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