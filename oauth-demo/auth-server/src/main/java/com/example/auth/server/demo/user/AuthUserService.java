package com.example.auth.server.demo.user;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthUserService {
    private final AuthUserRepository repository;
    private final PasswordEncoder encoder;

    @PostConstruct
    private void init() {
        AuthUserEntity user = new AuthUserEntity(
                "kouzie",
                encoder.encode("password"),
                "kouzie@test.com",
                "user"
        );
        repository.save(user);
        AuthUserEntity admin = new AuthUserEntity(
                "admin",
                encoder.encode("password"),
                "admin@test.com",
                "admin"
        );
        repository.save(admin);
    }

    @Transactional(readOnly = true)
    public Optional<AuthUserEntity> findByUname(String uname) {
        return repository.findByUname(uname);
    }

    @Transactional
    public AuthUserEntity save(AuthUserEntity authUserEntity) {
        return repository.save(authUserEntity);
    }
}
