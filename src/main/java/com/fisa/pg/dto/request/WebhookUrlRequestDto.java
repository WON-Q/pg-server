package com.fisa.pg.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MERCHANT Webhook URL 등록 또는 수정 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookUrlRequestDto {

    private String webhookUrl;
}
