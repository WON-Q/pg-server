package com.fisa.pg.dto.response;

import com.fisa.pg.entity.user.Merchant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 가맹점용 웹훅 생성 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWebhookResponseDto {

    /**
     * 웹훅 url
     */
    private String url;

    /**
     * 웹훅 상태
     * <br/>
     * true: 활성화, false: 비활성화
     */
    private boolean enabled;

    /**
     * 웹훅 비밀키
     * <br/>
     * 웹훅 요청 시 서명 검증을 위한 비밀키
     */
    private String secretKey;

    public static CreateWebhookResponseDto from(Merchant merchant) {
        return CreateWebhookResponseDto.builder()
                .url(merchant.getWebhookUrl())
                .enabled(merchant.isWebhookEnabled())
                .secretKey(merchant.getWebhookSecretKey())
                .build();
    }
}