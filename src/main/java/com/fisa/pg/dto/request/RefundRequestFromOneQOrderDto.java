package com.fisa.pg.dto.request;

import lombok.*;

/**
 * 환불 요청을 위한 DTO 클래스
 * 원큐오더 서버에서 PG 서버로 환불 요청을 보낼 때 사용됩니다.
 * <p>
 * 환불 요청 시 필요한 결제 ID를 포함합니다.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestFromOneQOrderDto {

    /**
     * 환불 요청을 위한 결제 ID
     */
    private Long paymentId;
}