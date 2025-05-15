package com.fisa.pg.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 클라이언트(가맹점)으로부터 PG 서버로 전달되는 결제 수단 선택 API의 응답 DTO
 * <br />
 * 이 DTO는 결제 흐름 중 <b>12번째 단계</b>에서 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodUpdateResponseDto {

    /**
     * 특정 결제 수단으로 결제를 진행할 수 있는 페이지가 있는 URL (예: https://pg.fisa.com/ui/wooricard/pay/2001)
     */
    private String redirectUrl;

}
