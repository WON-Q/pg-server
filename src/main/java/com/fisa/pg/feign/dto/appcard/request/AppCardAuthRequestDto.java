package com.fisa.pg.feign.dto.appcard.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PG사가 앱카드로 인증 요청을 보낼 때 전달하는 DTO
 * <br/>
 * 이 DTO는 결제 흐름 중 <b>16번째 단계</b>에서 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppCardAuthRequestDto {

    /**
     * 트랜잭션 ID
     */
    private String txnId;

    /**
     * 결제 금액
     */
    private Long amount;

    /**
     * 가맹점 ID
     */
    private Long merchantId;

    /**
     * 주문 ID
     */
    private String orderId;

    /**
     * 콜백 URL (결제 완료 후 리다이렉트)
     */
    private String callbackUrl;

}
