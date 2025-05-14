package com.fisa.pg.feign.dto.wonq.request;

import com.fisa.pg.entity.payment.PaymentStatus;
import com.fisa.pg.entity.transaction.TransactionStatus;
import com.fisa.pg.feign.dto.appcard.request.AppCardPaymentAuthResultDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 앱카드 인증 실패 시 PG가 원큐서버에게 반환하는 응답 정보
 * <br/>
 * 이 DTO는 결제 흐름 중 <b>33단계</b>에서 인증 실패 시 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppCardPaymentAuthFailDto {

    /**
     * 트랜잭션 ID
     */
    private String txnId;

    /**
     * 결제 상태 (FAILED)
     */
    private PaymentStatus paymentStatus;

    /**
     * 트랜잭션 상태 (AUTH_FAILED)
     */
    private TransactionStatus transactionStatus;

    /**
     * 결제 실패 DTO를 생성합니다.
     *
     * @param authResult 앱카드 인증 결과 정보
     * @return 인증 실패 응답 DTO
     */
    public static AppCardPaymentAuthFailDto from(AppCardPaymentAuthResultDto authResult) {
        return AppCardPaymentAuthFailDto.builder()
                .txnId(authResult.getTxnId())
                .paymentStatus(PaymentStatus.FAILED)
                .transactionStatus(TransactionStatus.AUTH_FAILED)
                .build();
    }
}