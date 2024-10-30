package com.example.auth.resourceserver.demo.model;

import org.springframework.data.repository.CrudRepository;

public interface AuthUserDetailRepository extends CrudRepository<AuthUserDetailEntity, String> {
}
