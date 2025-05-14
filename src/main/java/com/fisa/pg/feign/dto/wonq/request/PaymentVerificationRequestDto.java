package com.fisa.pg.feign.dto.wonq.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerificationRequestDto {
    private Long paymentId;
    private String orderId;
    private Long merchantId;
}