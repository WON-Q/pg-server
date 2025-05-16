package com.fisa.pg.exception;

/**
 * 로그인 인증 실패 시 발생하는 예외
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("이메일 또는 비밀번호가 일치하지 않습니다");
    }

}