package com.fisa.pg.dto.response;

import lombok.*;

/**
 * 가맹점 JWT 발급 응답 DTO
 * <ul>
 *     <li>name: API 키 이름</li>
 *     <li>accessKey: Access Key ID (공개 키 역할)</li>
 *     <li>secretKey: Secret Key (서명 비밀 키)</li>
 *     <li>validFrom: 키 유효 시작일 (YYYY-MM-DD)</li>
 *     <li>validTo: 키 만료일 (YYYY-MM-DD)</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantJwtResponseDto {

    /**
     * API 키 이름
     */
    private String name;

    /**
     * Access Key ID (공개 키 역할)
     */
    private String accessKey;

    /**
     * Secret Key (서명 비밀 키)
     * - 이 화면에서만 1회 노출
     */
    private String secretKey;

    /**
     * 키 유효 시작일 (YYYY-MM-DD)
     */
    private String validFrom;

    /**
     * 키 만료일 (YYYY-MM-DD)
     */
    private String validTo;
}
