package com.example.securitydemo.common.service;

import com.example.securitydemo.common.model.Member;
import com.example.securitydemo.common.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository repository;

    @Transactional(readOnly = true)
    public Optional<Member> findByUname(String uname) {
        return repository.findByUname(uname);
    }

    @Transactional
    public Member save(Member member) {
        return repository.save(member);
    }
}
