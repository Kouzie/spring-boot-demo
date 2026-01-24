package com.example.nats.component.jetstream;

import io.nats.client.JetStream;
import io.nats.client.Message;
import io.nats.client.MessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JetStreamMessageHandler implements MessageHandler {

    private final JetStream jetStream;

    @Override
    public void onMessage(Message msg) {
        String subject = msg.getSubject();
        if ("jetstream.nats.test.fail".equals(subject)) {
            handleDlqTestMessage(msg);
        } else if ("jetstream.nats.dlq".equals(subject)) {
            handleDlqMessage(msg);
        } else {
            handleGeneralMessage(msg);
        }
    }

    private void handleDlqTestMessage(Message msg) {
        try {
            log.info("Received message: {}, deliveryCount: {}", new String(msg.getData()),
                    msg.metaData().deliveredCount());
            // 테스트를 위해 무조건 예외 발생 (3번 실패 유도)
            if (true)
                throw new RuntimeException("Intentional Failure for DLQ Test");
        } catch (Exception e) {
            long deliverCount = msg.metaData().deliveredCount();

            if (deliverCount >= 3) {
                log.error("Max retries reached ({}). Moving to DLQ...", deliverCount);
                try {
                    // DLQ Subject로 발행
                    jetStream.publish("jetstream.nats.dlq", msg.getData());
                    msg.term(); // 더 이상 배달하지 않음 (Terminate)
                } catch (Exception ex) {
                    log.error("Failed to move to DLQ", ex);
                }
            } else {
                log.warn("Processing failed ({}). Sending Nak...", deliverCount);
                msg.nak(); // 재전송 요청
            }
        }
    }
    private void handleDlqMessage(Message msg) {
        log.error(">>> [DLQ RECEIVED] Subject: {}, Payload: {}", msg.getSubject(),
                new String(msg.getData()));
        msg.ack();
    }

    private void handleGeneralMessage(Message msg) {
        log.info("JetStream message received - subject:{}, message:{}",
                msg.getSubject(), new String(msg.getData()));
        msg.ack();
    }
}
