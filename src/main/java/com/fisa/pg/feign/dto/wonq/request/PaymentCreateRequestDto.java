package com.fisa.pg.feign.dto.wonq.request;

import com.fisa.pg.entity.payment.Payment;
import com.fisa.pg.feign.dto.wonq.response.PaymentCreateResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 클라이언트(가맹점)로부터 PG 서버로 전달되는 결제 생성 요청 DTO
 * 결제 흐름 상 5번(결제 준비 요청)에 해당
 * 결제 통화(currency)는 기본값 KRW를 사용
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCreateRequestDto {

    /**
     * 가맹점 내에서 고유한 주문 ID
     */
    private String orderId;

    /**
     * 결제를 요청한 가맹점의 고유 ID
     */
    private Long merchantId;

    /**
     * 결제 금액 (단위: 원)
     */
    private Long amount;

    /**
     * 결제 통화 (예: KRW) -> Default: KRW
     */
    private String currency;

    /**
     * Payment 엔티티로부터 응답 DTO를 생성하는 정적 팩토리 메서드
     *
     * @param payment 결제 엔티티
     * @param merchantId 가맹점 ID
     * @return 생성된 응답 DTO
     */
    public static PaymentCreateResponseDto from(Payment payment, Long merchantId) {
        return PaymentCreateResponseDto.builder()
                .paymentId(payment.getId())
                .merchantId(merchantId)
                .paymentStatus(payment.getPaymentStatus())
                .amount(payment.getAmount())
                .callbackUrl("/api/payment/ui/" + payment.getId())
                .build();
    }
}