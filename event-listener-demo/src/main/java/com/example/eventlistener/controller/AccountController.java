package com.example.eventlistener.controller;

import com.example.eventlistener.dto.AccountDto;
import com.example.eventlistener.dto.CreateAccountRequestDto;
import com.example.eventlistener.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
    private final AccountService service;

    @PostMapping
    public AccountDto createAccount(@RequestBody @Valid CreateAccountRequestDto requestDto) {
        AccountDto dto = service.createAccount(requestDto);
        return dto;
    }

    @GetMapping
    public List<AccountDto> getAllAccount() {
        return service.getAllAccount();
    }

    @GetMapping("/{id}")
    public AccountDto getAccountById(@PathVariable Long id) {
        return service.getAccountById(id);
    }
}
