package com.example.sharding.routing.demo.controller;

import com.example.sharding.routing.demo.config.Snowflake;
import com.example.sharding.routing.demo.config.ThreadLocalDatabaseContextHolder;
import com.example.sharding.routing.demo.dto.AccountDto;
import com.example.sharding.routing.demo.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.TransactionManager;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
    private final AccountService service;
    private final Snowflake snowflake;
    private final TransactionManager transactionManager; // JpaTransaction

    @GetMapping("/{accountId}")
    public AccountDto getAccount(@PathVariable Long accountId) {
        log.info("tm type:{}", transactionManager.getClass().getSimpleName());
        ThreadLocalDatabaseContextHolder.setById(accountId);
        return service.getAccountById(accountId);
    }

    @GetMapping
    public List<AccountDto> getAllAccount() {
        return service.getAllAccount();
    }

    @PostMapping
    public AccountDto addRandomAccount() {
        long accountId = snowflake.nextId();
        ThreadLocalDatabaseContextHolder.setById(accountId);
        return service.addRandomAccount(accountId);
    }
}
