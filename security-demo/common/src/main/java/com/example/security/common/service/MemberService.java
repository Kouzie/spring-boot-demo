package com.example.security.common.service;

import com.example.security.common.model.MemberEntity;
import com.example.security.common.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository repository;

    @Transactional(readOnly = true)
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    public MemberEntity findById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    @Transactional(readOnly = true)
    public Optional<MemberEntity> findByUname(String uname) {
        return repository.findByUname(uname);
    }

    @Transactional(readOnly = true)
    @PostAuthorize("returnObject.isPresent() && returnObject.get().uname == authentication.name && returnObject.get().uname == #uname")
    public Optional<MemberEntity> findByUnameSecurity(String uname) {
        return repository.findByUname(uname);
    }

    @Transactional
    public MemberEntity save(MemberEntity member) {
        return repository.save(member);
    }
}
