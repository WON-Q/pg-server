package com.fisa.pg.batch.job_config;

import com.fisa.pg.batch.utils.CsvGenerator;
import com.fisa.pg.batch.utils.SftpUploader;
import com.fisa.pg.entity.payment.Payment;
import com.fisa.pg.entity.payment.PaymentStatus;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.repository.PaymentRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class GenerateAndSendPurchaseCsvJob {

    private final EntityManagerFactory entityManagerFactory;
    private final PaymentRepository paymentRepository;
    private final CsvGenerator csvGenerator;
    private final SftpUploader sftpUploader;

    public GenerateAndSendPurchaseCsvJob(EntityManagerFactory entityManagerFactory,
                                         PaymentRepository paymentRepository,
                                         CsvGenerator csvGenerator,
                                         SftpUploader sftpUploader) {
        this.entityManagerFactory = entityManagerFactory;
        this.paymentRepository = paymentRepository;
        this.csvGenerator = csvGenerator;
        this.sftpUploader = sftpUploader;
    }

    @Bean
    public Job generateAndSendPurchaseCsvJob(JobRepository jobRepository, Step merchantCsvStep) {
        return new JobBuilder("GenerateAndSendPurchaseCsvJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(merchantCsvStep)
                .build();
    }

    @Bean
    public Step merchantCsvStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                ItemReader<Merchant> merchantReader,
                                ItemProcessor<Merchant, Merchant> merchantProcessor,
                                ItemWriter<Merchant> merchantWriter) {
        return new StepBuilder("GenerateCsvAndSendSftpStep", jobRepository)
                .<Merchant, Merchant>chunk(5000, transactionManager)
                .reader(merchantReader)
                .processor(merchantProcessor)
                .writer(merchantWriter)
                .faultTolerant()
                .retry(IOException.class)
                .retryLimit(3)
                .build();
    }

    @Bean
    public JpaPagingItemReader<Merchant> merchantReader() {
        return new JpaPagingItemReaderBuilder<Merchant>()
                .name("ReadMerchantStep")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT m FROM Merchant m WHERE m.isActive = true")
                .pageSize(10)
                .build();
    }

    @Bean
    public ItemProcessor<Merchant, Merchant> merchantProcessor() {
        return merchant -> merchant; // 정산 시간 필터링이 필요하면 여기에 구현
    }

    @Bean
    public ItemWriter<Merchant> merchantWriter() {
        return merchants -> {
            for (Merchant merchant : merchants) {
                List<Payment> payments = paymentRepository.findByMerchantAndPaymentStatusAndApprovedAtBetween(
                        merchant,
                        PaymentStatus.SUCCEEDED,
                        LocalDate.now().atStartOfDay(),
                        LocalDate.now().plusDays(1).atStartOfDay()
                );

                if (payments.isEmpty()) continue;

                File csv = csvGenerator.generate(merchant, payments);
                sftpUploader.upload(csv);
            }
        };
    }
}
