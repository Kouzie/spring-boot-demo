package com.example.security.common.service;

import com.example.security.common.config.MemberRole;
import com.example.security.common.model.MemberAuthEntity;
import com.example.security.common.repository.MemberAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberAuthService {
    private final MemberAuthRepository repository;

    public MemberAuthEntity save(MemberAuthEntity memberAuthEntity) {
        return repository.save(memberAuthEntity);
    }

    public MemberAuthEntity findById(MemberRole role) {
        return repository.findById(role).orElseThrow();
    }
}
