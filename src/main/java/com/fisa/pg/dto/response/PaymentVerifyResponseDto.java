package com.fisa.pg.dto.response;

import com.fisa.pg.entity.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 클라이언트(가맹점)으로부터 PG 서버로 전달되는 결제 검증 응답 DTO
 * <br />
 * 이 DTO는 결제 흐름 중 <b>46번째 단계</b>에서 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerifyResponseDto {

    /**
     * 결제 검증을 위한 트랜잭션 ID
     */
    private String transactionId;

    /**
     * 결제 검증 상태
     */
    private PaymentStatus status;

}
