package com.example.sharding.sphere.demo.controller;

import com.example.sharding.sphere.demo.dto.AccountDto;
import com.example.sharding.sphere.demo.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
    private final AccountService service;

    @GetMapping("/{accountId}")
    public AccountDto getAccount(@PathVariable Long accountId) {
        return service.getAccountById(accountId);
    }

    @GetMapping("/name/{name}")
    public List<AccountDto> getAccountByName(@PathVariable String name) {
        return service.getAccountByNameLike(name);
    }

    @PostMapping
    public AccountDto addRandomAccount() {
        return service.addRandomAccount();
    }
}
