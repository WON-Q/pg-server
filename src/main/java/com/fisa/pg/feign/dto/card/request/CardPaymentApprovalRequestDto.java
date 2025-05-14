package com.fisa.pg.feign.dto.card.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PG사에서 카드사로 결제 승인 요청을 전송하는 DTO
 * <br/>
 * 이 DTO는 결제 흐름 중 <b>33번째 단계</b>에서 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardPaymentApprovalRequestDto {

    /**
     * 트랜잭션 ID
     */
    private String transactionId;

    /**
     * 결제 금액
     */
    private Long amount;

    /**
     * 가맹점 ID
     */
    private Long merchantId;

    /**
     * 카드 번호
     */
    private String cardNumber;

    /**
     * 카드 승인에 필요한 데이터로부터 승인 요청 DTO를 생성하는 정적 팩토리 메서드
     *
     * @param txnId 트랜잭션 ID
     * @param amount 결제 금액
     * @param merchantId 가맹점 ID
     * @param cardNumber 카드 번호
     * @return 생성된 카드 승인 요청 DTO
     */
    public static CardPaymentApprovalRequestDto from(String txnId, Long amount, Long merchantId, String cardNumber) {
        return CardPaymentApprovalRequestDto.builder()
                .transactionId(txnId)
                .amount(amount)
                .merchantId(merchantId)
                .cardNumber(cardNumber)
                .build();
    }

}
