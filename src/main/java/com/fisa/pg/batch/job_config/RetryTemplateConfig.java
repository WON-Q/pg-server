package com.fisa.pg.batch.job_config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;

@Configuration
public class RetryTemplateConfig {

    // Tasklet 모델 재시도 전략(Chunk 모델과 달리 프레임워크에서 재시도 기능을 제공해주지 않음)
    @Bean
    public org.springframework.retry.support.RetryTemplate retryTemplate() {
        org.springframework.retry.support.RetryTemplate template = new org.springframework.retry.support.RetryTemplate();

        SimpleRetryPolicy policy = new SimpleRetryPolicy();
        policy.setMaxAttempts(3);                // 총 3회 시도

        FixedBackOffPolicy backOff = new FixedBackOffPolicy();
        backOff.setBackOffPeriod(2000);          // 시도 간격 2초

        template.setRetryPolicy(policy);
        template.setBackOffPolicy(backOff);
        return template;
    }
}