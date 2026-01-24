package com.example.nats.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JetStreamMessageDto {
    private String subject;
    private String message;
}
