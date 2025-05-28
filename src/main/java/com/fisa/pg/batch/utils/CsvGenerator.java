package com.fisa.pg.batch.utils;

import com.fisa.pg.batch.dto.MerchantSummary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class CsvGenerator {

    @Value("${batch.output.directory}")
    private String outputDirectory;

    /**
     * 전체 요약 리스트로 단일 CSV 파일 생성
     */
    public File generateSummary(List<MerchantSummary> summaries) throws IOException {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        String fileName = String.format("pg_daily_settlement_summary_to_card_%s.csv", timestamp);

        File dir = new File(outputDirectory);
        if (!dir.exists()) dir.mkdirs();
        File csvFile = new File(dir, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            // 헤더
            writer.write("merchantId,merchantName,total_amount,currency,settlement_date");
            writer.newLine();

            // 데이터
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (MerchantSummary ms : summaries) {
                writer.write(String.join(",",
                        ms.merchantId(),         // record 접근자
                        ms.merchantName(),
                        ms.totalAmount().toPlainString(),
                        ms.currency(),
                        ms.settlementDate().format(dtf)
                ));
                writer.newLine();
            }
        }
        return csvFile;
    }
}
