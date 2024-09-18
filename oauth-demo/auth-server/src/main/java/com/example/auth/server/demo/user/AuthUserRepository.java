package com.example.auth.server.demo.user;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AuthUserRepository extends CrudRepository<AuthUserEntity, Long> {

    Optional<AuthUserEntity> findByUname(String uname);
}
