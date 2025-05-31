package com.fisa.pg.batch.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class QuartzJobLauncher extends QuartzJobBean {

    @Autowired
    private JobLauncher jobLauncher;

    // Bean 이름(메서드명)으로 등록된 Spring Batch Job을 주입
    @Autowired
    @Qualifier("generateAndSendPurchaseCsvJob")
    private Job generateAndSendPurchaseCsvJob;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            jobLauncher.run(
                    generateAndSendPurchaseCsvJob,
                    new JobParametersBuilder()
                            .addLong("timestamp", System.currentTimeMillis())
                            .toJobParameters()
            );
        } catch (Exception ex) {
            // Quartz의 JobExecutionException을 던져야 Quartz가 재시도/오류 로깅을 합니다.
            throw new JobExecutionException(ex);
        }
    }
}