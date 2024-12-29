package com.example.security.common.repository;

import com.example.security.common.config.MemberRole;
import com.example.security.common.model.MemberAuthEntity;
import org.springframework.data.repository.CrudRepository;

public interface MemberAuthRepository extends CrudRepository<MemberAuthEntity, MemberRole> {
}
