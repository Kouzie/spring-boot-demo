package com.example.kafka.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTopicsReq {
    private String topic;
    private Integer numPartitions;
    private Short replicationFactor;
}
