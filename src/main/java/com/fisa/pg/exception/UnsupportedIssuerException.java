package com.fisa.pg.exception;

/**
 * 지원하지 않는 카드 발급사의 결제 요청이 들어왔을 때 발생하는 예외
 */
public class UnsupportedIssuerException extends RuntimeException {

    public UnsupportedIssuerException(String transactionId, String cardNumber) {
        super(String.format("지원하지 않는 카드 발급사입니다. (거래 : %s, 카드번호 : %s)", transactionId, cardNumber));
    }
}
