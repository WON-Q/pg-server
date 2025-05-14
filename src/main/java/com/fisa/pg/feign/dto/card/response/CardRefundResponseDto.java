package com.fisa.pg.feign.dto.card.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardRefundResponseDto {
    private boolean success;
    private String message;
    private String refundId;
}