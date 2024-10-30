package com.example.auth.resourceserver.demo.model;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthUserDetailService {
    private final AuthUserDetailRepository repository;

    @PostConstruct
    private void init() {
        AuthUserDetailEntity user = new AuthUserDetailEntity(
                "kouzie", // uname
                "010-1234-5678",
                "male",
                "kozuie_nickname",
                "1996-09-18"
        );
        repository.save(user);
        AuthUserDetailEntity admin = new AuthUserDetailEntity(
                "admin",
                "010-1111-2222",
                "female",
                "admin_nickname",
                "1998-02-03"
        );
        repository.save(admin);
    }

    @Transactional(readOnly = true)
    public AuthUserDetailEntity getUserById(String uname) {
        return repository.findById(uname).orElseThrow();
    }
}
