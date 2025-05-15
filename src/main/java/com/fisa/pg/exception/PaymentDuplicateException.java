package com.fisa.pg.exception;

/**
 * 결제 중복 발생 시 터지는 예외
 */
public class PaymentDuplicateException extends RuntimeException {

    public PaymentDuplicateException(String orderId, String merchantId) {
        super(String.format("이미 존재하는 결제입니다. 주문번호: %s, 가맹점ID: %s", orderId, merchantId));
    }
}
