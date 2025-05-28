package com.fisa.pg.batch.job_config;

import com.fisa.pg.batch.dto.MerchantSummary;
import com.fisa.pg.batch.utils.CsvGenerator;
import com.fisa.pg.batch.utils.SftpUploader;
import com.fisa.pg.entity.payment.Payment;
import com.fisa.pg.entity.payment.PaymentStatus;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.repository.MerchantRepository;
import com.fisa.pg.repository.PaymentRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class GenerateAndSendPurchaseCsvJob {

    private final PaymentRepository paymentRepository;
    private final MerchantRepository merchantRepository;
    private final CsvGenerator csvGenerator;
    private final SftpUploader sftpUploader;

    public GenerateAndSendPurchaseCsvJob(
            PaymentRepository paymentRepository,
            MerchantRepository merchantRepository,
            CsvGenerator csvGenerator,
            SftpUploader sftpUploader
    ) {
        this.paymentRepository = paymentRepository;
        this.merchantRepository = merchantRepository;
        this.csvGenerator = csvGenerator;
        this.sftpUploader = sftpUploader;
    }

    @Bean
    public Job generateAndSendPurchaseCsvJob(JobRepository jobRepository,
                                             Step settlementStep) {
        return new JobBuilder("GenerateAndSendPurchaseCsvJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(settlementStep)
                .build();
    }

    @Bean
    public Step settlementStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               org.springframework.retry.support.RetryTemplate retryTemplate) {

        return new StepBuilder("SettlementSummaryStep", jobRepository)
                .tasklet((contrib, ctx) ->
                                retryTemplate.execute(retryCtx -> {
                                    // 1) 어제 00:00:00 ~ 23:59:59
                                    LocalDate yesterday = LocalDate.now().minusDays(1);
                                    LocalDateTime start = yesterday.atStartOfDay();
                                    LocalDateTime end = yesterday.atTime(23, 59, 59);

                                    // 2) 정산 시각: 오늘 02:00
                                    LocalDateTime settlementTime = LocalDateTime.now()
                                            .withHour(2).withMinute(0).withSecond(0).withNano(0);

                                    // 3) 활성 가맹점 조회
                                    List<Merchant> merchants = merchantRepository.findByIsActiveTrue();

                                    // 4) 일일 매출 합계 계산 및 요약 DTO 생성
                                    List<MerchantSummary> summaries = new ArrayList<>();
                                    for (Merchant m : merchants) {
                                        List<Payment> payments = paymentRepository
                                                .findByMerchantAndPaymentStatusAndApprovedAtBetween(
                                                        m, PaymentStatus.SUCCEEDED, start, end);
                                        if (payments.isEmpty()) continue;

                                        BigDecimal total = payments.stream()
                                                .map(p -> BigDecimal.valueOf(p.getAmount()))
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                                        summaries.add(new MerchantSummary(
                                                m.getBusinessNumber(),
                                                m.getName(),
                                                total,
                                                payments.get(0).getCurrency(),
                                                settlementTime
                                        ));
                                    }

                                    // 5) CSV 생성 & SFTP 전송
                                    File csv = csvGenerator.generateSummary(summaries);
                                    sftpUploader.upload(csv);

                                    return RepeatStatus.FINISHED;
                                })
                        , transactionManager)
                .build();
    }
}