package com.example.quartz.schedule.job;

import com.example.quartz.service.TestService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
@RequiredArgsConstructor
public class HelloJob implements Job {
    private final TestService service;
    private final MeterRegistry meterRegistry;


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Counter jobCounter = meterRegistry.counter("job.execution.count", "jobName", "MyJob");
        log.info("RequestContractJob execute invoked, job-detail-key:{}, fired-time:{}", context.getJobDetail().getKey(), context.getFireTime());
        service.test();
        log.info("RequestContractJob execute complete");
        jobCounter.increment();
    }
}
