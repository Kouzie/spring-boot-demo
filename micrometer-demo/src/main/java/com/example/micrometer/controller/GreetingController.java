package com.example.micrometer.controller;

import com.example.micrometer.dto.HelloJava;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RestController
@RequestMapping("/greeting")
@RequiredArgsConstructor
public class GreetingController {
    private final ObjectMapper objectMapper;

    // PrometheusMeterRegistry
    // prometheus micrometer exporter
    private final MeterRegistry registry;
    private Counter counter;
    private Gauge gauge;
    private AtomicLong atomicLong = new AtomicLong(0);
    private DistributionSummary summary;
    private Timer timer;

    @PostConstruct
    private void init() {
        // Counter 설정
        this.counter = Counter.builder("api.call.count")
                .description("api call count")
                .tags("team", "monitoring", "deploy_version", "dev")
                .register(registry);

        // Gauge 설정
        this.gauge = Gauge.builder("result.sum", this.atomicLong, AtomicLong::get)
                .description("result sum")
                .register(registry);

        // summary
        this.summary = DistributionSummary.builder("request.num.size")
                .baseUnit("num")
                .register(registry);

        // timer
        this.timer = Timer
                .builder("test.timer")
                .tags("team", "monitoring", "deploy_version", "dev")
                .publishPercentiles(0.8, 0.9, 1.0)
                .publishPercentileHistogram()
                .serviceLevelObjectives(
                        Duration.ofMillis(200), // 200ms
                        Duration.ofMillis(400), // 400ms
                        Duration.ofMillis(600), // 600ms
                        Duration.ofMillis(800), // 800ms
                        Duration.ofMillis(1000) // 1000ms
                )
                .register(registry);

    }

    @GetMapping
    public String greet() throws JsonProcessingException {
        log.info("greet invoked");
        HelloJava helloJava = new HelloJava("Hello", LocalDateTime.now());
        counter.increment(1);
        return objectMapper.writeValueAsString(helloJava);
    }

    @GetMapping("/summary/{num}")
    public String summary(@PathVariable Integer num) {
        atomicLong.set(num.longValue());
        summary.record(num);
        String result = "";
        result += "Total count: " + summary.count() + "\n";
        result += "Total sum: " + summary.totalAmount() + "\n";
        result += "Average: " + summary.mean() + "\n";
        result += "Maximum: " + summary.max() + "\n";
        return result;
    }

    @GetMapping("/record")
    public String record() throws InterruptedException {
        // 작업 시작 전 시간 측정
        long startTime = System.nanoTime();
        Thread.sleep(1000); // 측정할 이벤트
        long endTime = System.nanoTime();
        timer.record(endTime - startTime, TimeUnit.NANOSECONDS);

        // 작업 수행 후 Timer에서 정보 가져오기
        String result = "";
        result += "총 실행 횟수: " + timer.count() + "\n";
        result += "평균 실행 시간(ms): " + timer.mean(TimeUnit.MILLISECONDS) + "\n";
        result += "최대 실행 시간(ms): " + timer.max(TimeUnit.MILLISECONDS) + "\n";
        result += "총 실행 시간(ms): " + timer.totalTime(TimeUnit.MILLISECONDS) + "\n";
        return result;
    }
}
