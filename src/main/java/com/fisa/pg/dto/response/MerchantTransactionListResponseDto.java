package com.fisa.pg.dto.response;

import com.fisa.pg.entity.transaction.Transaction;
import com.fisa.pg.entity.transaction.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 가맹점 사용자의 트랜잭션 목록 조회 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantTransactionListResponseDto {

    /**
     * 트랜잭션 ID
     */
    private String transactionId;

    /**
     * 가맹점 이름
     */
    private String merchantName;

    /**
     * 결제 금액
     */
    private int amount;

    /**
     * 결제 상태
     */
    private TransactionStatus status;

    /**
     * 결제 방법
     */
    private String method;

    /**
     * 결제 승인 시간
     */
    private LocalDateTime approvedAt;

    /**
     * 트랜잭션 엔티티를 DTO로 변환하는 메서드
     * @return MerchantTransactionListResponseDto
     */
    public static MerchantTransactionListResponseDto from(Transaction tx) {
        return MerchantTransactionListResponseDto.builder()
                .transactionId(tx.getTransactionId())
                .amount(tx.getAmount().intValue())
                .status(tx.getTransactionStatus())
                .method(tx.getMethod().name())
                .approvedAt(tx.getApprovedAt())
                .build();
    }
}
