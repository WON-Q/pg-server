package com.fisa.pg.feign.dto.wonq.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PG사가 원큐 오더 서버로 딥링크를 포함한 결제 시작 응답을 전송할 때 사용하는 DTO
 * <br/>
 * 이 DTO는 결제 흐름 중 <b>20번째 단계</b>에서 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
 */
@Data
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
     * 앱 실행 딥링크 URL
     */
    private String deepLink;
}