package com.example.sharding.routing.demo.service;

import com.example.sharding.routing.demo.RandomTestUtil;
import com.example.sharding.routing.demo.config.Snowflake;
import com.example.sharding.routing.demo.config.ThreadLocalDatabaseContextHolder;
import com.example.sharding.routing.demo.dto.AccountDto;
import com.example.sharding.routing.demo.repository.AccountEntity;
import com.example.sharding.routing.demo.repository.AccountRepository;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final Snowflake snowflake;

    private final HikariDataSource dataSource;

    // @PostConstruct
    private void init() {
        for (int i = 0; i < 10; i++) {
            long accountId = snowflake.nextId();
            ThreadLocalDatabaseContextHolder.setById(accountId);
            AccountEntity account = new AccountEntity(
                    accountId,
                    RandomTestUtil.generateRandomString(16));
            accountRepository.save(account);
        }
    }


    @Transactional(readOnly = true)
    public AccountDto getAccountById(Long accountId) {
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        log.info("is read only:{}", isReadOnly);
        AccountDto result = accountRepository.findByAccountId(accountId)
                .map(this::toDto)
                .orElseThrow();
        return result;
    }

    @Transactional(readOnly = true)
    public List<AccountDto> getAllAccount() {
        return accountRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public AccountDto addRandomAccount(Long accountId) {
        AccountEntity accountEntity = new AccountEntity(accountId, RandomTestUtil.generateRandomString(10));
        accountRepository.save(accountEntity);
        return toDto(accountEntity);
    }

    private AccountDto toDto(AccountEntity entity) {
        AccountDto dto = new AccountDto();
        dto.setAccountId(entity.getAccountId());
        dto.setUserName(entity.getUserName());
        dto.setCreateTime(entity.getCreateTime());
        return dto;
    }

    @Transactional
    public void checkConnections(HikariDataSource dataSource) {
        int totalConnections = dataSource.getHikariPoolMXBean().getTotalConnections();      // 전체 연결 개수 (사용 중 + 유휴)
        int activeConnections = dataSource.getHikariPoolMXBean().getActiveConnections();    // 활성(사용 중인) 연결 개수
        int idleConnections = dataSource.getHikariPoolMXBean().getIdleConnections();        // 유휴 연결 개수
        Connection connection = DataSourceUtils.getConnection(dataSource);
        // Isolation Level을 확인
        try {
            int isolationLevel = connection.getTransactionIsolation();
            System.out.println("Current Isolation Level: " + isolationLevel);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        log.info("is read only:{}", isReadOnly);
        System.out.println("Total Connections: " + totalConnections);
        System.out.println("Active Connections: " + activeConnections);
        System.out.println("Idle Connections: " + idleConnections);
    }

}
