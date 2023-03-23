package com.example.websock.stomp.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Greeting {
    private LocalDateTime now;
    private String content;
}