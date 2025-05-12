package com.fisa.pg.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 관리자용 Webhook URL 전체 조회 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookUrlAdminResponseDto {

    /**
     * 가맹점 ID
     */
    private Long merchantId;

    /**
     * 가맹점 이름
     */
    private String merchantName;

    /**
     * Webhook URL
     */
    private String webhookUrl;
}
