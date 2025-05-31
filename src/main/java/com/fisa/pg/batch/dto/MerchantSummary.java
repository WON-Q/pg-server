package com.fisa.pg.batch.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 가맹점별 일일 정산 요약 정보를 담는 불변 객체
 */
public record MerchantSummary(
        String merchantId,
        String merchantName,
        BigDecimal creditAmount,
        BigDecimal debitAmount,
        BigDecimal totalAmount,
        String currency,
        LocalDateTime settlementDate
) {
}