package com.fisa.pg.exception;

/**
 * API 키를 찾을 수 없을 때 발생하는 예외
 */
public class ApiKeyNotFoundException extends ApiKeyException {

    public ApiKeyNotFoundException() {
        super("존재하지 않는 API 키입니다");
    }

}