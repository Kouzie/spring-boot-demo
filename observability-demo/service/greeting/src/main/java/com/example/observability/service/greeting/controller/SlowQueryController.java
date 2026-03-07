package com.example.observability.service.greeting.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * Prometheus JDBC Query 자동 모니터링 데모 Controller
 * 
 * 'datasource-micrometer'에 의해 모든 쿼리가 자동으로 측정됩니다.
 * 메트릭 확인: /actuator/prometheus -> 'jdbc_query_seconds' 키워드로 검색
 */
@Slf4j
@RestController
@RequestMapping("/slow-query")
@RequiredArgsConstructor
public class SlowQueryController {

    private final JdbcTemplate jdbcTemplate;

    /**
     * MySQL SLEEP 함수를 사용하여 의도적으로 지연을 발생시킵니다.
     * 이 쿼리 실행 시간은 자동으로 'jdbc.query' 메트릭으로 기록됩니다.
     * 바인딩 변수(?)를 사용하여 'SELECT SLEEP(?)' 라는 단일 태그로 집계되도록 합니다.
     */
    @GetMapping("/sleep/{seconds}")
    public String sleepQuery(@PathVariable int seconds) {
        int clampedSeconds = Math.min(seconds, 30);
        log.info("[SlowQuery] 자동 측정 시작 - SLEEP {}초", clampedSeconds);

        // 바인딩 변수를 사용해야 Prometheus에서 동일한 태그(SELECT SLEEP(?))로 묶입니다.
        jdbcTemplate.queryForObject("SELECT SLEEP(?)", Integer.class, clampedSeconds);

        log.info("[SlowQuery] 자동 측정 종료");
        return String.format("자동 측정 완료: %d초 대기됨\n" +
                "→ /actuator/prometheus 에서 'jdbc_query_seconds'를 확인하세요.\n" +
                "→ 'jdbc_query' 태그값이 'SELECT SLEEP(?)' 인지 확인하세요.", clampedSeconds);
    }
}
