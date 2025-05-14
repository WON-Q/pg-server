package com.fisa.pg.feign.dto.wonq.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 클라이언트(가맹점)로부터 PG 서버로 전달되는 결제 수단 DTO
 * 결제 흐름 상 10번(결제 수단 선택 요청)에 해당
 * 최종 결제 플로우 노션 dto 반영한 상태
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