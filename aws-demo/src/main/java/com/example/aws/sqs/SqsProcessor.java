package com.example.aws.sqs;

import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SqsProcessor {
    @SqsListener(value = "${queue.name}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void receiveMqttMessage(@Header("type") String type, String message) {
        log.info("mqtt message received, type:{}, message:{}", type, message);
    }
}
