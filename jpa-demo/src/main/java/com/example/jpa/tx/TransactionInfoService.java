package com.example.jpa.tx;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionInfoService {

    @PersistenceContext
    private EntityManager entityManager;

    public Map<String, Object> getSessionTransactionInfo() {
        String sql = "SELECT @@SESSION.transaction_isolation AS transaction_isolation, " +
                "@@SESSION.transaction_read_only AS transaction_read_only";
        Object[] result = (Object[]) entityManager.createNativeQuery(sql).getSingleResult();

        return Map.of(
                "transaction_isolation", result[0],
                "transaction_read_only", result[1]
        );
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    public Map<String, Object> getSessionTransactionInfoReadOnly() {
        String sql = "SELECT @@SESSION.transaction_isolation AS transaction_isolation, " +
                "@@SESSION.transaction_read_only AS transaction_read_only";
        Object[] result = (Object[]) entityManager.createNativeQuery(sql).getSingleResult();

        return Map.of(
                "transaction_isolation", result[0],
                "transaction_read_only", result[1]
        );
    }
}
