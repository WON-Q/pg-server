package com.fisa.pg.dto.response;

import com.fisa.pg.entity.transaction.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponseDto {
    private Long paymentId;
    private String txnId;
    private Long refundAmount;
    private TransactionStatus refundStatus;
    private LocalDateTime refundedAt;
    private boolean success;
    private String message;
}