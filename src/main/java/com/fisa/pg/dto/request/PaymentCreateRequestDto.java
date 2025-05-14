package com.fisa.pg.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 클라이언트(가맹점)으로부터 PG 서버로 전달되는 결제 생성 요청 DTO
 * <br />
 * 이 DTO는 결제 흐름 중 <b>5번째 단계</b>에서 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

}