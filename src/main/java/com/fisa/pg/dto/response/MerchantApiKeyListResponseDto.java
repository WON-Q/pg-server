package com.fisa.pg.dto.response;

import com.fisa.pg.entity.auth.ApiKey;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 가맹점 API 키 목록 응답 DTO
 * <br />
 * 이 DTO는 가맹점 API 키 목록 조회 시 사용됩니다.
 */
@Getter
@Builder
public class MerchantApiKeyListResponseDto {
    private List<ApiKeyInfo> apiKeys;

    @Getter
    @Builder
    public static class ApiKeyInfo {

        /**
         * API 키 ID
         */
        private Long id;

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

        /**
         * API 키 상태
         * <ul>
         *     <li>active: 활성화된 키</li>
         *     <li>expiring: 만료 예정인 키</li>
         *     <li>expired: 만료된 키</li>
         * </ul>
         */
        private String status;

        /**
         * ApiKey 엔티티로부터 API 키 정보 DTO를 생성합니다.
         *
         * @param apiKey API 키 엔티티
         * @return 생성된 API 키 정보 DTO
         */
        public static ApiKeyInfo from(ApiKey apiKey) {
            return ApiKeyInfo.builder()
                    .id(apiKey.getId())
                    .name(apiKey.getName())
                    .accessKey(apiKey.getAccessKey())
                    .isActive(apiKey.isActive())
                    .issuedAt(apiKey.getIssuedAt())
                    .expiresAt(apiKey.getExpiresAt())
                    .status(computeStatus(apiKey.getExpiresAt()))
                    .build();

        }

        /**
         * API 키의 만료일을 기준으로 상태를 계산합니다.
         *
         * @param expiresAt API 키 만료일
         * @return "active" | "expiring" | "expired"
         */
        private static String computeStatus(LocalDateTime expiresAt) {
            if (expiresAt == null) return "active";

            LocalDateTime now = LocalDateTime.now();
            if (expiresAt.isBefore(now)) return "expired";

            long daysLeft = java.time.Duration.between(now, expiresAt).toDays();
            return daysLeft <= 30 ? "expiring" : "active";
        }

    }

    /**
     * API 키 정보 목록으로부터 응답 DTO를 생성합니다.
     *
     * @param apiKeyInfos API 키 정보 목록
     * @return 생성된 응답 DTO
     */
    public static MerchantApiKeyListResponseDto from(List<ApiKeyInfo> apiKeyInfos) {
        return MerchantApiKeyListResponseDto.builder()
                .apiKeys(apiKeyInfos)
                .build();
    }
}