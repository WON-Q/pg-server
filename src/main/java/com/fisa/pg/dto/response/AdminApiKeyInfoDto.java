package com.fisa.pg.dto.response;

import com.fisa.pg.entity.auth.ApiKey;
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
public class AdminApiKeyInfoDto {
    private Long id;
    private String merchantName;
    private String name;
    private String publicKey;
    private boolean isActive;
    private String issuedAt;
    private String expiresAt;

    public static AdminApiKeyInfoDto from(ApiKey entity) {
        return AdminApiKeyInfoDto.builder()
                .id(entity.getId())
                .merchantName(entity.getMerchant().getName())
                .name(entity.getName())
                .publicKey(entity.getPublicKey())
                .isActive(entity.isActive())
                .issuedAt(entity.getIssuedAt().toString())
                .expiresAt(entity.getExpiresAt() != null ? entity.getExpiresAt().toString() : "-")
                .build();
    }
}
