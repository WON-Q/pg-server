package com.fisa.pg.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PG사에서 카드사로 결제 환불 요청을 전송하는 DTO
 * <br/>
 * 이 DTO는 결제 흐름 중 <b>환불 요청</b> 단계에서 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestDto {
    private Long paymentId;
    private Long refundAmount;
    private String reason;
}