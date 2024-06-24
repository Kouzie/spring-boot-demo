package com.example.sharding.sphere.demo.service;

import com.example.sharding.sphere.demo.RandomTestUtil;
import com.example.sharding.sphere.demo.dto.AccountDto;
import com.example.sharding.sphere.demo.repository.AccountEntity;
import com.example.sharding.sphere.demo.repository.AccountRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.sharding.algorithm.keygen.SnowflakeKeyGenerateAlgorithm;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository repository;

    // private final SnowflakeKeyGenerateAlgorithm snowflakeKeyGenerateAlgorithm;
    @PostConstruct
    private void init() {

    }

    @Transactional
    public AccountDto addRandomAccount() {
        AccountEntity accountEntity = new AccountEntity(RandomTestUtil.generateRandomString(10));
        accountEntity = repository.save(accountEntity);
        return toDto(accountEntity);
    }

    @Transactional(readOnly = true)
    public AccountDto getAccountById(Long accountId) {
        return repository.findByAccountId(accountId)
                .map(this::toDto)
                .orElseThrow();
    }

    @Transactional(readOnly = true)
    public AccountDto getRandomAccount() {
        List<AccountEntity> accountEntityList = repository.findAll();
        if (accountEntityList.isEmpty()) {
            return null; // 리스트가 비어있을 경우 null 반환 (혹은 적절한 예외 처리)
        }
        int randomIndex = RandomTestUtil.generateRandomInteger(accountEntityList.size());
        return toDto(accountEntityList.get(randomIndex));
    }

    private AccountDto toDto(AccountEntity entity) {
        AccountDto dto = new AccountDto();
        dto.setAccountId(entity.getAccountId());
        dto.setUserName(entity.getUserName());
        dto.setCreateTime(entity.getCreateTime());
        return dto;
    }

}
