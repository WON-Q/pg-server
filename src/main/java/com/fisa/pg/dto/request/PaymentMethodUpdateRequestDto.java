package com.fisa.pg.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 클라이언트(가맹점)으로부터 PG 서버로 전달되는 결제 수단 선택 API의 요청 DTO
 * <br />
 * 이 DTO는 결제 흐름 중 <b>10번째 단계</b>에서 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodUpdateRequestDto {

    /**
     * 결제 ID
     */
    private Long paymentId;

    /**
     * 결제 수단 (예: WOORI_APP_CARD 등)
     */
    private String method;

}
