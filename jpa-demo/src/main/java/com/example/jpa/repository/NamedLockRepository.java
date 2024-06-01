package com.example.jpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NamedLockRepository {
    private final JdbcTemplate jdbcTemplate;

    public Integer getLock(String lockName, int timeout) {
        // timeout 은 락을 획득하기 위해 기다리는 시간(초)
        Integer result = jdbcTemplate.queryForObject(
                "SELECT GET_LOCK(?, ?)",
                Integer.class, // return type
                lockName, timeout // params
        );
        return result;
    }

    public Integer releaseLock(String lockName) {
        Integer result = jdbcTemplate.queryForObject(
                "SELECT RELEASE_LOCK(?)",
                Integer.class,
                lockName
        );
        return result;
    }

}