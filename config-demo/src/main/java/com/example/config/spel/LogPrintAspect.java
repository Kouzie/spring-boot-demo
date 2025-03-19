package com.example.config.spel;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class LogPrintAspect {

    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(logPrint)")
    public Object logExecution(ProceedingJoinPoint joinPoint, LogPrint logPrint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();

        // SpEL에서 사용할 변수 설정, context 는 stateful 함으로 매번 생성해야함
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        // SpEL 표현식 평가
        String prefix = parser.parseExpression(logPrint.prefix()).getValue(context, String.class);

        log.info("[{}] 실행 시작: {}", prefix, method.getName());
        Object result = joinPoint.proceed();
        log.info("[{}] 실행 종료: {}", prefix, method.getName());

        return result;
    }
}
