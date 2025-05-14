package com.fisa.pg.feign.dto.wonq.response;

import com.fisa.pg.entity.payment.Payment;
import com.fisa.pg.entity.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerificationResponseDto {
    private Long paymentId;
    private String orderId;
    private Long amount;
    private PaymentStatus status;
    private boolean verified;
    private String message;

    /**
     * 결제 검증 결과로부터 응답 DTO를 생성하는 정적 팩토리 메서드
     *
     * @param payment 결제 엔티티
     * @param verified 검증 성공 여부
     * @param message 검증 결과 메시지
     * @return 생성된 결제 검증 응답 DTO
     */
    public static PaymentVerificationResponseDto from(Payment payment, boolean verified, String message) {
        return PaymentVerificationResponseDto.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getPaymentStatus())
                .verified(verified)
                .message(message)
                .build();
    }
}