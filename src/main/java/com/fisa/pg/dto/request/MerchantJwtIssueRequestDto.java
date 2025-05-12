package com.fisa.pg.dto.request;

import lombok.*;

/**
 * 가맹점 JWT 발급 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantJwtIssueRequestDto {

    /**
     * API 키 이름
     */
    private String name;

    /**
     * 유효기간 (예: 30일, 90일, 180일, 1년, 직접 입력)
     */
    private int validDays;

    public static MerchantJwtIssueRequestDto of(String name, int validDays) {
        return MerchantJwtIssueRequestDto.builder()
                .name(name)
                .validDays(validDays)
                .build();
    }
}


