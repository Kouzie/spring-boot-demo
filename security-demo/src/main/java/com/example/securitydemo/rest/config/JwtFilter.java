package com.example.securitydemo.rest.config;

import com.example.securitydemo.common.config.CustomSecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // check request header JWT
        final String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (authorization == null || !authorization.startsWith(BEARER)) {
            log.warn("JWT Token does not begin with Bearer String, url:{}", request.getRequestURL());
            request.setAttribute("exception", "INVALID AUTHORIZATION HEADER"); //
        } else {
            // generate auth object & save at security context
            String jwtToken = authorization.substring(7);
            Map<String, Object> claims = JwtTokenUtil.getAllClaimsFromToken(jwtToken);
            Authentication authentication = getAuthentication(claims); // generate auth object
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    private Authentication getAuthentication(Map<String, Object> claims) {
        Long uid = Long.valueOf(claims.getOrDefault("uid", 0).toString());
        String subject = claims.getOrDefault("sub", "").toString();
        List<String> roles = (List<String>) claims.get("roles");
        UserDetails userDetails = new CustomSecurityUser(uid, subject, roles);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
