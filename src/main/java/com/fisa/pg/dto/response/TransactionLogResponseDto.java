package com.fisa.pg.dto.response;

import com.fisa.pg.entity.payment.PaymentMethod;
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
     * 트랜잭션 로그 ID
     */
    private String transactionId;

    /**
     * 트랜잭션 로그 메시지
     */
    private String message;

    /**
     * 트랜잭션을 구성하는 금액
     */
    private Long amount;

    /**
     * 트랜잭션 상태
     */
    private TransactionStatus status;

    /**
     * 지불 방법
     */
    private PaymentMethod method;

    /**
     * 트랜잭션 발생 시각
     */
    private LocalDateTime date;

    /**
     * 가맹점 이름
     */
    private String merchantName;

    public static TransactionLogResponseDto from(TransactionLog transactionLog) {
        return TransactionLogResponseDto.builder()
                .transactionId(transactionLog.getTransaction().getTransactionId())
                .message(transactionLog.getMessage())
                .amount(transactionLog.getTransaction().getAmount())
                .status(transactionLog.getStatus())
                .method(transactionLog.getTransaction().getPayment().getPaymentMethod())
                .date(transactionLog.getTransaction().getRequestedAt())
                .merchantName(transactionLog.getMerchant().getName())
                .build();
    }
}
