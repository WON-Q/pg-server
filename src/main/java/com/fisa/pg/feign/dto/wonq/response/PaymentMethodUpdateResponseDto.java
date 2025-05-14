package com.fisa.pg.feign.dto.wonq.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 결제 수단 선택 이후, 결제 수단 UI
 * 결제 흐름 11번, 12번에 해당
 * 최종 결제 플로우 노션 dto 반영한 상태
 */
@Getter
@Builder
public class PaymentMethodUpdateResponseDto {

    /**
     * 선택한 결제 수단의 UI 또는 redirect URL
     * 예: https://pg.fisa.com/ui/wooricard/pay/2001
     */
    private String redirectUrl;

}