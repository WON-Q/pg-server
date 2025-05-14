package com.fisa.pg.feign.dto.wonq.request;

import com.fisa.pg.entity.payment.PaymentStatus;
import com.fisa.pg.entity.transaction.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 지원하지 않는 카드 발급사 검증 실패 시 응답 DTO
 * <br/>
 * 인증 성공 후 BIN 정보 확인 단계에서 지원하지 않는 카드사인 경우 사용
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardIssuerValidationFailDto {

    /**
     * 트랜잭션 ID
     */
    private String txnId;

    /**
     * 인증 여부 (false)
     */
    private boolean authenticated;

    /**
     * 결제 상태 (FAILED)
     */
    private PaymentStatus paymentStatus;

    /**
     * 트랜잭션 상태 (FAILED)
     */
    private TransactionStatus transactionStatus;

    /**
     * 카드 발급사
     */
    private String issuer;

    /**
     * 실패 메시지
     */
    private String message;

    /**
     * 지원하지 않는 카드사 응답 생성
     *
     * @param txnId 트랜잭션 ID
     * @param issuer 카드 발급사
     * @return 카드 발급사 검증 실패 응답 DTO
     */
    public static CardIssuerValidationFailDto unsupportedIssuer(String txnId, String issuer) {
        return CardIssuerValidationFailDto.builder()
                .txnId(txnId)
                .authenticated(false)
                .paymentStatus(PaymentStatus.FAILED)
                .transactionStatus(TransactionStatus.FAILED)
                .issuer(issuer)
                .message("지원하지 않는 카드사입니다: " + issuer)
                .build();
    }
}