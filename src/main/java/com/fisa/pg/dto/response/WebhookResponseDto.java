package com.fisa.pg.dto.response;

import com.fisa.pg.entity.user.Merchant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 웹훅 조회 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookResponseDto {

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

    /**
     * Webhook 활성화 여부
     */
    private boolean isActive;

    /**
     * Webhook 등록일
     */
    private String createdAt;

    /**
     * Webhook 조회 응답 DTO를 생성하는 정적 메서드
     *
     * @param merchant 가맹점 정보
     * @return 등록된 Webhook 정보
     */
    public static WebhookResponseDto from(Merchant merchant) {
        return WebhookResponseDto.builder()
                .merchantId(merchant.getId())
                .merchantName(merchant.getName())
                .webhookUrl(merchant.getWebhookUrl())
                .isActive(merchant.getIsWebhookEnabled())
                .createdAt(merchant.getCreatedAt().toString())
                .build();
    }
}
