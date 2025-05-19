package com.fisa.pg.exception;

/**
 * API 키 관련 공통 예외 클래스
 */
public abstract class ApiKeyException extends RuntimeException {

    public ApiKeyException(String message) {
        super(message);
    }

}