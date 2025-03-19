package com.example.config.spel;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpelComponent {
    private final LogTestService service;
    // bean 의 property 로부터 값 가져오기
    // @Value 값은 초기화 이후 설정됨으로 get 함수를 사용해서 가져와야함.
    @Value("#{systemPropertiesConfig.getAppName()}")
    private String appName;
    ExpressionParser parser = new SpelExpressionParser();

    @PostConstruct
    public void parse() {
        log.info(appName);

        // 1. 기본적인 수식 평가
        int result = parser.parseExpression("10 * 2 + 5").getValue(Integer.class);
        log.info("결과: " + result); // 25

        // 2. 문자열 조작
        String message = parser.parseExpression("'Hello ' + 'Spring!'").getValue(String.class);
        log.info("메시지: " + message); // Hello Spring!

        // 3. 객체 속성 접근
        SpelPerson person = new SpelPerson("John", 30);

        StandardEvaluationContext context = new StandardEvaluationContext(person);
        Integer age = parser.parseExpression("age").getValue(context, Integer.class);
        log.info("나이: " + age); // 30

        service.execute("test-user-id", "hello world");
    }
}