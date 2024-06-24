package com.example.sharding.routing.demo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@RequiredArgsConstructor
public class DemoDataSourceRouter extends AbstractRoutingDataSource {

    TransactionManager transactionManager;

    // 세션의 정보를 확인, @Transaction 어노테이션으로부터 생김
    @Override
    protected Object determineCurrentLookupKey() {
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        DemoDatabase datasource = ThreadLocalDatabaseContextHolder.getClientDatabase();
        log.info(">>>>>> current data source : {}, isReadOnly: {}", datasource, isReadOnly);
        return datasource;
    }
}
