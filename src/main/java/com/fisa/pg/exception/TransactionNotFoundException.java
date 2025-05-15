package com.fisa.pg.exception;

/**
 * 거래가 존재하지 않을 때 발생하는 예외
 */
public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(String transactionId) {
        super(String.format("존재하지 않는 거래입니다. (%s)", transactionId));
    }
}
