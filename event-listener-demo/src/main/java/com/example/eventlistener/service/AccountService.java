package com.example.eventlistener.service;

import com.example.eventlistener.dto.AccountDto;
import com.example.eventlistener.dto.CreateAccountRequestDto;
import com.example.eventlistener.dto.AccountCreateEvent;
import com.example.eventlistener.repository.AccountEntity;
import com.example.eventlistener.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AccountDto createAccount(CreateAccountRequestDto dto) {
        log.info("createAccount invoked");
        AccountEntity entity = toEntity(dto);
        entity = repository.save(entity);
        log.info("account save end");
        AccountDto result = toDto(entity);
        eventPublisher.publishEvent(new AccountCreateEvent(result));
        // if (new Random().nextInt() % 2 == 0)
        //     throw new IllegalArgumentException("temp exception");
        log.info("account return");
        return result;
    }

    @Transactional(readOnly = true)
    public AccountDto getAccountById(Long id) {
        return repository.findById(id)
                .map(this::toDto)
                .orElseThrow();
    }

    private AccountDto toDto(AccountEntity entity) {
        AccountDto dto = new AccountDto();
        dto.setAccountId(entity.getAccountId());
        dto.setName(entity.getName());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setCreated(entity.getCreated());
        dto.setUpdated(entity.getUpdated());
        return dto;
    }

    private AccountEntity toEntity(CreateAccountRequestDto dto) {
        AccountEntity entity = new AccountEntity(
                dto.getName(),
                dto.getUsername(),
                dto.getEmail());
        return entity;
    }

    public List<AccountDto> getAllAccount() {
        return repository.findAll().stream()
                .map(this::toDto)
                .toList();
    }
}
