package com.fisa.pg.feign.dto.wonq.request;

import com.fisa.pg.entity.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResultNotificationDto {
    private String orderId;
    private Long paymentId;
    private String txnId;
    private Long amount;
    private PaymentStatus status;
    private String paymentMethod;
    private String cardNumber;
    private String approvedAt;

    // 앱카드 결제 승인 결과로부터 객체 생성
    public static PaymentResultNotificationDto from(Long paymentId, String orderId,
            String txnId, Long amount, PaymentStatus status,
            String cardNumber, String approvedAt) {
        return PaymentResultNotificationDto.builder()
                .paymentId(paymentId)
                .orderId(orderId)
                .txnId(txnId)
                .amount(amount)
                .status(status)
                .paymentMethod("APP_CARD")
                .cardNumber(cardNumber)
                .approvedAt(approvedAt)
                .build();
    }
}