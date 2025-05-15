package com.fisa.pg.feign.dto.wonq.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자에게 전달될 딥링크를 전송하는 요청 DTO
 * <br/>
 * 이 DTO는 결제 흐름 중 <b>20번째 단계</b>에서 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
 * </p>
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeepLinkPaymentRequestDto {

    /**
     * 주문 ID
     */
    private String orderId;

    /**
     * 결제 ID
     */
    private String paymentId;

    /**
     * 앱카드 실행을 위한 딥링크
     */
    private String deepLink;

}
