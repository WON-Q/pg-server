package com.fisa.pg.exception;

/**
 * 가맹점을 찾을 수 없을 때 발생하는 예외
 */
public class MerchantNotFoundException extends RuntimeException {

    public MerchantNotFoundException(Long merchantId) {
        super("가맹점을 찾을 수 없습니다. ID: " + merchantId);
    }
}