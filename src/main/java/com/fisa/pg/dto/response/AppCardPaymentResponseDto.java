package com.fisa.pg.dto.response;

import com.fisa.pg.entity.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PG사가 앱카드 서버에게 결제 결과를 반환할 때 사용되는 DTO
 * <br/>
 * 이 DTO는 결제 흐름 중 <b>38번째 단계</b>에서 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppCardPaymentResponseDto {

    /**
     * 트랜젝션 ID
     */
    private String txnId;

    /**
     * 결제 상태
     */
    private PaymentStatus paymentStatus;

}
