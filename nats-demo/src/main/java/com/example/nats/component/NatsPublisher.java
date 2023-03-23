package com.example.nats.component;

import com.example.nats.dto.TopicMessageDto;
import io.nats.client.Connection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class NatsPublisher {

    private final Connection natsConnection;

    /**
     * @Param topic     the subject to publish the message to
     * @Param message   the message payload
     */
    public void publish(String topic, String message) {
        natsConnection.publish(topic, message.getBytes());
    }

    /**
     * @Param topic     the subject to publish the message to
     * @Param message   the message payload
     * @Param reply     the subject to which subscribers should send responses
     */
    public void publish(String topic, String message, String reply) {
        natsConnection.publish(topic, reply, message.getBytes());
    }


    public void publish(TopicMessageDto topicMessage) {
        if (StringUtils.hasText(topicMessage.getReply())) {
            publish(topicMessage.getTopic(), topicMessage.getMessage(), topicMessage.getReply());
        }
        else {
            publish(topicMessage.getTopic(), topicMessage.getMessage());
        }
    }
}
