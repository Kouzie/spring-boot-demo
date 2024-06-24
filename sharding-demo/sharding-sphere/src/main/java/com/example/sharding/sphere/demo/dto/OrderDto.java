package com.example.sharding.sphere.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class OrderDto {
    private Long orderId;
    private String title;
    private Long accountId;
    private OffsetDateTime createTime;
}
