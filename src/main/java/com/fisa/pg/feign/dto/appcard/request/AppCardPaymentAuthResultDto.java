package com.fisa.pg.feign.dto.appcard.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 앱카드 서버가 PG사로 결제 인증 결과와 승인 요청 정보를 전달할 때 사용하는 DTO
 * <br/>
 * 이 DTO는 결제 흐름 중 <b>32번째 단계</b>에서 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppCardPaymentAuthResultDto {

    /**
     * 트랜잭션 ID
     */
    private String txnId;

    /**
     * 인증 성공 여부
     */
    private boolean authenticated;

    /**
     * 인증 완료 시각
     */
    private String authenticatedAt;

    /**
     * 카드 번호
     */
    private String cardNumber;

    /**
     * 카드 유형 (예: CREDIT, CHECK)
     */
    private String cardType;

}