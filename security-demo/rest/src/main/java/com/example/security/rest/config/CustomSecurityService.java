package com.example.security.rest.config;

import com.example.security.common.config.CustomSecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service("cssecu")
public class CustomSecurityService {

    public boolean hasAccess(Authentication authentication, Long uid) {
        CustomSecurityUser user  = (CustomSecurityUser) authentication.getPrincipal();
        log.info("cssecu hasAccess invoked, username:{}, uid:{}", user.getUsername(),  user.getUid());
        return uid.equals(user.getUid());
    }
}