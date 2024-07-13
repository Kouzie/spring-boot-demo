package com.example.sharding.sphere.demo.repository;

import com.example.sharding.sphere.demo.dto.AccountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    Optional<AccountEntity> findByAccountId(Long accountId);

    @Query("SELECT ae FROM AccountEntity ae WHERE ae.userName LIKE %:keyword%")
    List<AccountEntity> findAllByUserNameLike(@Param("keyword") String keyword);
}
