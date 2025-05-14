package com.fisa.pg.feign.dto.card.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardRefundRequestDto {
    private String txnId;
    private String originalTxnId;
    private Long amount;
    private String reason;
}