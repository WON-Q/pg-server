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
    private String txnId;

    /**
     * 결제 금액
     */
    private Long amount;

    /**
     * 정산받을 가맹점 계좌번호
     */
    private String settlementAccountNumber;

    /**
     * 카드 번호
     */
    private String cardNumber;

    /**
     * 트랜잭션 정보와 카드 정보를 기반으로 카드 결제 승인 요청 DTO를 생성하는 정적 팩토리 메서드
     *
     * @param transactionId 트랜잭션 ID
     * @param amount 결제 금액
     * @param settlementAccountNumber 가맹점 정산 계좌번호
     * @param cardNumber 전체 카드 번호
     * @return 카드 결제 승인 요청 DTO
     */
    public static CardPaymentApprovalRequestDto from(String transactionId, Long amount, String settlementAccountNumber, String cardNumber) {
        return CardPaymentApprovalRequestDto.builder()
                .txnId(transactionId)
                .amount(amount)
                .settlementAccountNumber(settlementAccountNumber)
                .cardNumber(cardNumber)
                .build();
    }


}
