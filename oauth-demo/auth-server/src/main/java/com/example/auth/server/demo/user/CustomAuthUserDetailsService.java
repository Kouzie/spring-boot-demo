package com.example.auth.server.demo.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomAuthUserDetailsService implements UserDetailsService {

    private final AuthUserService authUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("CustomSecurityUsersService loadUserByUsername invoked, username:{}", username);
        AuthUserEntity authUserEntity = authUserService.findByUname(username)
                .orElseThrow(IllegalArgumentException::new);
        return toUser(authUserEntity);
    }

    private UserDetails toUser(AuthUserEntity entity) {
        String username = entity.getUname();
        String password = entity.getUpw();
        Long uid = entity.getUid();
        String uname = entity.getUname();
        String email = entity.getEmail();
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(entity.getRole()));
        CustomAuthUser user = new CustomAuthUser(
                uid, uname, email, username, password, authorities);
        return user;
    }
}