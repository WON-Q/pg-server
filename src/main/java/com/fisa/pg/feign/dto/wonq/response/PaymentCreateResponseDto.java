package com.fisa.pg.feign.dto.wonq.response;


import com.fisa.pg.entity.payment.Payment;
import com.fisa.pg.entity.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 결제 초기화 응답 DTO
 * 결제 흐름 상 7번에 해당
 * 최종 결제 플로우 노션 dto 반영한 상태
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
     * */
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
