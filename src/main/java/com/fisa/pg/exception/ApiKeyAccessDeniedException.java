package com.fisa.pg.exception;

/**
 * API 키에 대한 접근 권한이 없을 때 발생하는 예외 클래스
 */
public class ApiKeyAccessDeniedException extends ApiKeyException {

    public ApiKeyAccessDeniedException() {
        super("해당 API 키에 대한 접근 권한이 없습니다");
    }

}