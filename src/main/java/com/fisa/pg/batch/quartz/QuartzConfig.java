package com.fisa.pg.batch.quartz;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail settlementJobDetail() {
        return JobBuilder.newJob(QuartzJobLauncher.class)
                .withIdentity("dailySettlementJob")
                .usingJobData("jobName", "GenerateAndSendPurchaseCsvJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger settlementJobTrigger(JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("dailySettlementTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 * * ?"))  // 매일 02:00
                .build();
    }
}