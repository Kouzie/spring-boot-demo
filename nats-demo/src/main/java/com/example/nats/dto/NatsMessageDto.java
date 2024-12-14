package com.example.nats.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NatsMessageDto {
    private String subject;
    private String message;
    private String replyTo; // reply topic
}
