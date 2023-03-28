package com.example.securitydemo.common.repository;

import com.example.securitydemo.common.model.Member;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MemberRepository extends CrudRepository<Member, Long> {

    Optional<Member> findByUname(String uname);
}
