package com.example.nats.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopicMessageDto {
    private String topic;
    private String message;
    private String reply; // reply topic
}
