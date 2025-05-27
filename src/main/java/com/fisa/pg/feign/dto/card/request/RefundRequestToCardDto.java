package com.fisa.pg.feign.dto.card.request;

import lombok.*;

/**
 * PG 서버로부터 카드사 서버로 보내는 환불 요청 DTO
 */
@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestToCardDto {

    /**
     * 환불 요청 트랜잭션 ID
     */
    private String txnId;
}