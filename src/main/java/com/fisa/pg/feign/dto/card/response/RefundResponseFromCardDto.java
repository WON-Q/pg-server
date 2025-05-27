package com.fisa.pg.feign.dto.card.response;

import com.fisa.pg.entity.payment.PaymentStatus;
import lombok.*;

/**
 * 카드사 서버로부터 PG 서버로 보내는 환불 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponseFromCardDto {

    /**
     * 환불 응답 트랜잭션 ID
     */
    private String txnId;

    /**
     * 환불 응답의 결제 상태
     */
    private PaymentStatus paymentStatus;

}