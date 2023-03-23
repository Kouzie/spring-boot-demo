package org.example.admin.customer.config;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

// @Component 로 filter 를 빈으로 등록하면 servlet filter 에 자동 등록고
// 스프링 시큐리티에서도 설정하기에 중복 등록되기에 사용하면 안된다.
// servlet filter 는 시큐리티의 ignore 설정도 적용되지 않음으로 servlet filter 는 등록지 않을 필요가 있다.
// SecurityConfig 의 FilterRegistrationBean 참고
public class RequestFilter extends OncePerRequestFilter {
// OncePerRequestFilter 하나의 request 당 필터가 한번만 실행되는 것을 보장

    private static final String API_KEY = "api-key";

    private final String apiKey;

    public RequestFilter(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // x-api-key 를 가지고 있을 경우 system 호출
        final String apiKey = request.getHeader(API_KEY);
        if (apiKey != null && apiKey.equals(this.apiKey)) {
            Authentication authentication = getSystemAuthentication();
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
            return;
        } else {
            String msg = "api key does not exist";
            request.setAttribute("exception", msg);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
    }

    private Authentication getSystemAuthentication() {
        String username = "system";
        String role = "ROLE_SYSTEM";
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        UserDetails userDetails = new User(username, "no password", authorities);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        return usernamePasswordAuthenticationToken;
    }
}