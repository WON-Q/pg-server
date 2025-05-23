package com.fisa.pg.dto.response;

import com.fisa.pg.entity.transaction.Transaction;
import com.fisa.pg.entity.transaction.TransactionLog;
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
public class TransactionLogResponseDto {

    /**
     * 트랜잭션 ID
     */
    private String transactionId;

    /**
     * 결제 금액
     */
    private Long amount;

    /**
     * 상태 (한글로 표시)
     */
    private String status;

    /**
     * 결제 수단
     */
    private String method;

    /**
     * 트랜잭션 처리 시간
     */
    private LocalDateTime timestamp;

    /**
     * TransactionLog 엔티티로부터 응답 DTO 생성
     */
    public static TransactionLogResponseDto from(TransactionLog log) {
        Transaction transaction = log.getTransaction();

        return TransactionLogResponseDto.builder()
                .transactionId(transaction.getTransactionId())
                .amount(transaction.getAmount())
                .status(formatStatus(log.getStatus()))
                .method(formatMethod(transaction.getMethod().name()))
                .timestamp(log.getCreatedAt())
                .build();
    }

    /**
     * 상태값을 사용자 친화적인 한글 메시지로 변환
     */
    private static String formatStatus(TransactionStatus status) {
        return switch (status) {
            case PENDING -> "대기중";
            case CREATED -> "결제중";
            case APPROVED -> "성공";
            case FAILED -> "실패";
            case CANCELED -> "환불됨";
            case AUTH_FAILED -> "인증 실패";
        };
    }

    /**
     * 결제 수단을 사용자 친화적인 한글 메세지로 변환
     */
    private static String formatMethod(String method) {
        if (method == null) return "미지정";

        return switch (method) {
            case "WOORI_APP_CARD" -> "우리 앱카드";
            case "CREDIT_CARD" -> "신용카드";
            case "DEBIT_CARD" -> "체크카드";
            default -> method;
        };
    }
}