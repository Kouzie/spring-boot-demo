package com.example.sharding.sphere.demo.repository;

import com.example.sharding.sphere.demo.dto.AccountDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    Optional<AccountEntity> findByAccountId(Long accountId);
}
