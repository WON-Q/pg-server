package com.fisa.pg.dto.response;

import com.fisa.pg.entity.auth.ApiKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyResponseDto {

    /**
     * API 키 ID
     */
    private Long id;

    /**
     * 가맹점 ID
     */
    private Long merchantId;

    /**
     * 가맹점 이름
     */
    private String merchantName;

    /**
     * API 키 이름
     */
    private String name;

    /**
     * 액세스 키
     */
    private String accessKey;

    /**
     * 액세스 키 활성화 여부
     */
    private boolean isActive;

    /**
     * 발급 시각
     */
    private LocalDateTime issuedAt;

    /**
     * 만료 시각
     */
    private LocalDateTime expiresAt;

    /**
     * 마지막 사용 시각
     */
    private LocalDateTime lastUsed;


    public static ApiKeyResponseDto from(ApiKey apiKey) {
        return ApiKeyResponseDto.builder()
                .id(apiKey.getId())
                .merchantId(apiKey.getMerchant().getId())
                .merchantName(apiKey.getMerchant().getName())
                .name(apiKey.getName())
                .accessKey(apiKey.getAccessKey())
                .isActive(apiKey.isActive())
                .issuedAt(apiKey.getIssuedAt())
                .expiresAt(apiKey.getExpiresAt())
                .lastUsed(apiKey.getLastUsed())
                .build();
    }

}
