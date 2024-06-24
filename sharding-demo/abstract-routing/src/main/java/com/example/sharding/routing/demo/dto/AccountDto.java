package com.example.sharding.routing.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class AccountDto {
    private Long accountId;
    private String userName;
    private OffsetDateTime createTime;
}
