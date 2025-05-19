package com.fisa.pg.dto.response;

import com.fisa.pg.entity.auth.ApiKey;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * API 키 생성 응답 DTO
 * <br />
 * 이 DTO는 API 키 생성 시 사용됩니다.
 */
@Getter
@Builder
public class ApiKeyCreatedResponseDto {

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
     * 시크릿 키
     */
    private String secretKey;

    /**
     * 발급 시각
     */
    private LocalDateTime issuedAt;

    /**
     * 만료 시각
     */
    private LocalDateTime expiresAt;

    /**
     * ApiKey 엔티티로부터 응답 DTO를 생성합니다.
     *
     * @param apiKey API 키 엔티티
     * @return 생성된 API 키 응답 DTO
     */
    public static ApiKeyCreatedResponseDto from(ApiKey apiKey) {
        return ApiKeyCreatedResponseDto.builder()
                .id(apiKey.getId())
                .name(apiKey.getName())
                .accessKey(apiKey.getAccessKey())
                .secretKey(apiKey.getSecretKey())
                .issuedAt(apiKey.getIssuedAt())
                .expiresAt(apiKey.getExpiresAt())
                .build();
    }
}