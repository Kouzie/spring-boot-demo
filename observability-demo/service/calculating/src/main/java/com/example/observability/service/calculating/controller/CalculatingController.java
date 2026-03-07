package com.example.observability.service.calculating.controller;

import com.example.observability.service.calculating.adaptor.GreetingServiceRestAdaptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.bson.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/calculating")
public class CalculatingController {
    private final GreetingServiceRestAdaptor greetingServiceRestAdaptor;
    private final MongoTemplate mongoTemplate;

    @GetMapping("/greet")
    public String greet() {
        return greetingServiceRestAdaptor.getGreeting();
    }

    @GetMapping("/{num1}/{num2}")
    public Long addNumbers(@PathVariable Long num1, @PathVariable Long num2) {
        log.info("addNumbers invoked, num1:{}, num2:{}", num1, num2);
        return num1 + num2;
    }

    /**
     * MongoDB JavaScript $where 연산자를 사용하여 의도적으로 지연을 발생시킵니다.
     * 이 쿼리 실행 시간은 자동으로 'mongodb_driver_commands' 메트릭으로 기록됩니다.
     */
    @GetMapping("/mongo/sleep/{seconds}")
    public String mongoSleep(@PathVariable int seconds) {
        long ms = seconds * 1000L;
        log.info("[MongoDB] 자동 측정 시작 - SLEEP {}ms", ms);

        // 1. 데이터가 없으면 지연이 발생하지 않으므로, 임시 문서를 하나 생성하거나 확인합니다.
        String tempCollection = "slow_query_temp";
        if (mongoTemplate.count(new Query(), tempCollection) == 0) {
            mongoTemplate.insert(new Document("name", "dummy"), tempCollection);
        }

        // 2. 안전한 전용 컬렉션에서 $where 실행 (JavaScript sleep 활용)
        // 주의: $where는 성능에 좋지 않으며, 실서비스에서는 모니터링 테스트용으로만 사용해야 합니다.
        Query query = new Query(Criteria.where("$where").is("sleep(" + ms + ") || true"));

        try {
            mongoTemplate.find(query, Object.class, tempCollection);
        } catch (Exception e) {
            log.error("[MongoDB] 쿼리 실행 중 오류 발생", e);
            return "오류 발생: " + e.getMessage();
        }

        log.info("[MongoDB] 자동 측정 종료");
        return String.format("MongoDB 자동 측정 완료: %d초 대기됨\n" +
                "→ /actuator/prometheus 에서 'mongodb_driver_commands'를 확인하세요.", seconds);
    }
}
