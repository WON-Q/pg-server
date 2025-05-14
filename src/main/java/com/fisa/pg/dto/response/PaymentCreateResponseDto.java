package com.fisa.pg.dto.response;

import com.fisa.pg.entity.payment.Payment;
import com.fisa.pg.entity.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 클라이언트(가맹점)으로부터 PG 서버로 전달되는 결제 생성 응답 DTO
 * <br />
 * 이 DTO는 결제 흐름 중 <b>7번째 단계</b>에서 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateResponseDto {

    /**
     * 결제 ID
     */
    private Long paymentId;

    /**
     * 결제를 요청한 가맹점의 고유 ID
     */
    private Long merchantId;

    /**
     * 결제 상태 (예: CREATED, SUCCEEDED 등)
     */
    private PaymentStatus paymentStatus;

    /**
     * 결제 금액
     */
    private Long amount;

    /**
     * 결제창 UI
     */
    private String callbackUrl;

    /**
     * Payment 엔티티로부터 응답 DTO를 생성하는 정적 팩토리 메서드
     *
     * @param payment    결제 엔티티
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
