package com.example.eventlistener.event;

import com.example.eventlistener.dto.AccountCreateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Slf4j
@Component
public class DemoEventListener {

    @Async("taskExecutor")
    @EventListener
    public void handleAccountCreateEvent(AccountCreateEvent event) throws InterruptedException {
        log.info("Received custom event, account:" + event.getAccount());
    }

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAccountCreateTransactionEvent(AccountCreateEvent event) throws InterruptedException {
        log.info("Received custom transaction event, account:" + event.getAccount());
    }
}