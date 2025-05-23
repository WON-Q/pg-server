package com.fisa.pg.controller;

import com.fisa.pg.dto.request.PaymentVerifyRequestDto;
import com.fisa.pg.dto.response.BaseResponse;
import com.fisa.pg.dto.response.PaymentVerifyResponseDto;
import com.fisa.pg.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 원큐오더 서버로부터 PG 서버로 요청하는 결제 검증 API
 * <br />
 * 이 컨트롤러는 원큐오더 서버로부터 PG 서버로 전달되는 결제 검증 요청을 처리합니다.
 * <br />
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentVerificationController {

    private final PaymentService paymentService;

    @PostMapping("/verify")
    public ResponseEntity<BaseResponse<PaymentVerifyResponseDto>> verifyPayment(
            @RequestBody PaymentVerifyRequestDto request
    ) {
        log.info("결제 검증 요청 수신: transactionId={}", request.getTransactionId());
        PaymentVerifyResponseDto response = paymentService.verifyPayment(request);
        return ResponseEntity.ok(BaseResponse.onSuccess("결제 검증 성공", response));
    }
}