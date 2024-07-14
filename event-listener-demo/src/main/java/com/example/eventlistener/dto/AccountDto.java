package com.example.eventlistener.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Getter
@Setter
public class AccountDto {
    @Override
    public String toString() {
        return "AccountDto{" +
                "accountId=" + accountId +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    private Long accountId;
    private String name;
    private String username;
    private String email;
    private OffsetDateTime created;
    private OffsetDateTime updated;
}
