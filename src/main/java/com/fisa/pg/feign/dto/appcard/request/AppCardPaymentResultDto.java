package com.fisa.pg.feign.dto.appcard.request;

import com.fisa.pg.entity.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PG사에서 앱카드 서버로 결제 승인 결과를 전송하는 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppCardPaymentResultDto {

    /**
     * 트랜잭션 ID
     */
    private String txnId;

    /**
     * 결제 상태
     */
    private PaymentStatus paymentStatus;


    /**
     * 트랜잭션 ID와 결제 상태로부터 결과 DTO를 생성하는 정적 팩토리 메서드
     */
    public static AppCardPaymentResultDto from(String txnId, PaymentStatus paymentStatus) {
        return AppCardPaymentResultDto.builder()
                .txnId(txnId)
                .paymentStatus(paymentStatus)
                .build();
    }
}