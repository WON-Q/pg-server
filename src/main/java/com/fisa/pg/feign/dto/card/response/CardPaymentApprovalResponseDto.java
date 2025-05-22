package com.fisa.pg.feign.dto.card.response;

import com.fisa.pg.entity.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 카드사에서 PG로 반환하는 결제 승인 응답 정보
 * <br/>
 * 이 DTO는 결제 흐름 중 <b>36번째 단계</b>에서 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardPaymentApprovalResponseDto {

    /**
     * 트랜잭션 ID
     */
    private String txnId;

    /**
     * 결제 상태
     */
    private PaymentStatus paymentStatus;

}
