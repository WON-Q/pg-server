package com.fisa.pg.dto.response;

import com.fisa.pg.entity.payment.PaymentStatus;
import lombok.*;

/**
 * 환불 응답을 위한 DTO 클래스
 * PG 서버에서 원큐오더 서버로 환불 응답을 보낼 때 사용됩니다.
 * <p>
 * 환불 응답 시 필요한 결제 ID와 트랜잭션 ID, 결제 상태를 포함합니다.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponseToOneQOrderDto {

    /**
     * 환불 응답에 대한 결제 ID
     */
    private Long paymentId;

    /**
     * 환불 응답에 대한 트랜잭션 ID
     */
    private String txnId;

    /**
     * 환불 응답에 대한 결제 상태
     */
    private PaymentStatus paymentStatus;
}