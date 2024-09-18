package com.example.auth.server.demo.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

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
//        User user = new User(
//                entity.getUname(),
//                entity.getUpw(),
//                List.of(new SimpleGrantedAuthority(entity.getRole()))
//        );
        CustomAuthUser user = new CustomAuthUser(entity);
        return user;
    }
}