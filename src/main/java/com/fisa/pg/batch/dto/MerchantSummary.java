package com.fisa.pg.batch.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 상점별 정산 요약 정보를 담는 불변 객체
 */
public record MerchantSummary(
        String merchantId,
        String merchantName,
        BigDecimal totalAmount,
        String currency,
        LocalDateTime settlementDate
) {
}