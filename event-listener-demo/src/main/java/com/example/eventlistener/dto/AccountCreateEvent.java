package com.example.eventlistener.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AccountCreateEvent {
    private AccountDto account;
}
