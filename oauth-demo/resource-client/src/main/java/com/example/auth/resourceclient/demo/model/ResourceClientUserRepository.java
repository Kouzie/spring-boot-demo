package com.example.auth.resourceclient.demo.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceClientUserRepository extends JpaRepository<ResourceClientUserEntity, Long> {
}