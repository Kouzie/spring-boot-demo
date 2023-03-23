package com.example.quartz.schedule;//package com.example.quartz.schedule;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.quartz.spi.JobFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.quartz.SchedulerFactoryBean;
//
//@Slf4j
//@Configuration
//@RequiredArgsConstructor
//public class QuartzTestConfig {
//
//    @Bean
//    public JobFactory jobFactory(ApplicationContext applicationContext) {
//        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
//        jobFactory.setApplicationContext(applicationContext);
//        return jobFactory;
//    }
////
////    @Bean
////    SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory) {
////        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
////        schedulerFactoryBean.setAutoStartup(appQuartzEnable);
////        schedulerFactoryBean.setJobFactory(jobFactory);
////        if (appQuartzEnable) {
////            schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);
////        } else {
////            log.warn("Can't initialize Quartz because it's not enabled");
////        }
////        return schedulerFactoryBean;
////    }
//
//}
