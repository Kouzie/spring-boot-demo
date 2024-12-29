package com.example.security.common.repository;


import com.example.security.common.model.MemberEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MemberRepository extends CrudRepository<MemberEntity, Long> {

    Optional<MemberEntity> findByUname(String uname);
}
