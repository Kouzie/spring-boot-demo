package com.example.quartz.schedule;

import com.example.quartz.schedule.job.HelloJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuartzService {
    private final SchedulerFactoryBean schedulerFactory;

    @PostConstruct
    public void scheduled() throws SchedulerException {
        Scheduler scheduler = schedulerFactory.getScheduler();
        JobDataMap map1 = new JobDataMap(Collections.singletonMap("num", "1"));
        JobDataMap map2 = new JobDataMap(Collections.singletonMap("num", "2"));
        JobDetail job1 = jobDetail("hello1", "hello-group", map1);
        JobDetail job2 = jobDetail("hello2", "hello-group", map2);
        CronTrigger trigger1 = trigger("trigger1", "trigger-group");
        CronTrigger trigger2 = trigger("trigger2", "trigger-group");
        try {
//        if (!scheduler.checkExists(job1.getKey()))
            scheduler.scheduleJob(job1, trigger1);
//        if (!scheduler.checkExists(job2.getKey()))
            scheduler.scheduleJob(job2, trigger2);
        } catch (SchedulerException e) {
            System.out.println("error:" );
            e.printStackTrace();
        }
    }

    public JobDetail jobDetail(String name, String group, JobDataMap dataMap) {
        JobDetail job = JobBuilder.newJob(HelloJob.class)
                .withIdentity(name, group)
                .withDescription("simple hello job")
                .usingJobData(dataMap)
                .storeDurably(true)
                .build();
        return job;
    }

    public CronTrigger trigger(String name, String group) {
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(name, group)
                .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * ? * *"))
                .withDescription("hello my trigger")
                .build();
        return trigger;
    }
}
