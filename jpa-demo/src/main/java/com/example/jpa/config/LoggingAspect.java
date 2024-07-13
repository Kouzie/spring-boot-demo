package com.example.jpa.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(9999)
@Component
public class LoggingAspect {
    public static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);


    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }


    @Around("springBeanPointcut()")
    public Object methodTimeLogger(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        // Get intercepted method details
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        // Measure method execution time
        // StopWatch stopWatch = new StopWatch(className + "->" + methodName);
        // stopWatch.start(methodName);
        logger.info(className + "->" + methodName + " start!");
        Object result = proceedingJoinPoint.proceed();
        logger.info(className + "->" + methodName + " end!");
        //  stopWatch.stop();
        //  // Log method execution time
        //  if (logger.isInfoEnabled()) {
        //      logger.info(stopWatch.prettyPrint());
        //  }
        return result;
    }
}