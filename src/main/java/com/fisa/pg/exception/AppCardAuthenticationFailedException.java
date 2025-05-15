package com.fisa.pg.exception;

/**
 * 앱카드 인증이 실패했을 때 발생하는 예외
 */
public class AppCardAuthenticationFailedException extends RuntimeException {

    public AppCardAuthenticationFailedException(String transactionId) {
        super(String.format("앱 카드 인증이 실패했습니다. 트랜잭션 ID: %s", transactionId));
    }
}
