package com.example.auth.server.demo.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class CustomAuthUser extends User {

    private final Long uid;
    private final String uname;
    private final String email;

    public CustomAuthUser(
            long uid,
            String uname,
            String email,
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(username, password, authorities);
        this.uid = uid;
        this.uname = uname;
        this.email = email;
    }
}
