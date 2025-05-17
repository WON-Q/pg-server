package com.fisa.pg.advice;

import com.fisa.pg.dto.response.BaseResponse;
import com.fisa.pg.exception.InvalidCredentialsException;
import com.fisa.pg.exception.PaymentDuplicateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 전역 예외 처리 핸들러
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentDuplicateException.class)
    public ResponseEntity<BaseResponse<Void>> handlePaymentDuplicateException(PaymentDuplicateException e) {
        log.error("결제 중복 오류 발생: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(BaseResponse.onConflict(e.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidCredentialsException(InvalidCredentialsException e) {
        log.error("잘못된 인증 정보 오류 발생: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(BaseResponse.onUnauthorized(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleException(Exception e) {
        log.error("서버 오류 발생", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.onInternalServerError("서버 내부 오류가 발생했습니다."));
    }
}
