package com.fisa.pg.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 가맹점용 웹훅 생성 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWebhookRequestDto {

    /**
     * 웹훅 URL
     */
    private String url;

}
