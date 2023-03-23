package com.example.aws.sqs;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class MessageSender {

    private final Regions regions;
    private final AWSCredentialsProvider credentialsProvider;

    private QueueMessagingTemplate queueMessagingTemplate;

    @PostConstruct
    private void init() {
        AmazonSQSAsync amazonSQSAsync = AmazonSQSAsyncClientBuilder
                .standard()
                .withRegion(regions)
                .withCredentials(credentialsProvider)
                .build();
        queueMessagingTemplate = new QueueMessagingTemplate(amazonSQSAsync);
    }

    public void sendMessage(String serviceQueueName, String type, String data) {
        Message<String> message = MessageBuilder.withPayload(data)
                .setHeader("type", type)
                .build();
        queueMessagingTemplate.send(serviceQueueName, message);
    }
}
