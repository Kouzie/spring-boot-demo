package com.example.jpa.tx;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tx-info")
public class TransactionInfoController {
    private final TransactionInfoService transactionInfoService;

    // curl -X GET http://localhost:8080/tx-info
    @GetMapping
    public Map<String, Object> testTransactionInfo() {
        return transactionInfoService.getSessionTransactionInfo();
    }

    @GetMapping("/readonly")
    public Map<String, Object> testTransactionInfoReadOnly() {
        return transactionInfoService.getSessionTransactionInfoReadOnly();
    }
}
