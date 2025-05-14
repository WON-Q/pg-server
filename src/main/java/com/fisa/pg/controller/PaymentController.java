package com.fisa.pg.controller;

import com.fisa.pg.dto.BaseResponse;
import com.fisa.pg.entity.payment.PaymentStatus;
import com.fisa.pg.feign.dto.appcard.request.AppCardAuthRequestDto;
import com.fisa.pg.feign.dto.appcard.request.AppCardPaymentAuthResultDto;
import com.fisa.pg.feign.dto.card.response.CardPaymentApprovalResponseDto;
import com.fisa.pg.feign.dto.wonq.request.AppCardPaymentAuthFailDto;
import com.fisa.pg.feign.dto.wonq.request.PaymentCreateRequestDto;
import com.fisa.pg.feign.dto.wonq.request.PaymentMethodUpdateRequestDto;
import com.fisa.pg.feign.dto.wonq.request.PaymentVerificationRequestDto;
import com.fisa.pg.feign.dto.wonq.response.PaymentCreateResponseDto;
import com.fisa.pg.feign.dto.wonq.response.PaymentMethodUpdateResponseDto;
import com.fisa.pg.feign.dto.wonq.response.PaymentVerificationResponseDto;
import com.fisa.pg.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 준비를 요청받는 API
     * <br/>
     * 이 API는 결제 흐름 중 <b>5-7번째 단계</b>에서 사용됩니다.
     *
     * @param request 결제 준비 요청 정보
     * @return 결제 ID 및 관련 정보
     */
    @PostMapping("/prepare")
    public ResponseEntity<BaseResponse<PaymentCreateResponseDto>> preparePayment(
            @RequestBody PaymentCreateRequestDto request) {

        log.info("결제 준비 요청 수신: 주문ID={}, 가맹점ID={}, 금액={}",
                request.getOrderId(), request.getMerchantId(), request.getAmount());

        PaymentCreateResponseDto response = paymentService.createPayment(request);

        log.info("결제 준비 완료: 결제ID={}, 가맹점ID={}",
                response.getPaymentId(), response.getMerchantId());

        return ResponseEntity.ok(BaseResponse.success(response));
    }

    /**
     * 결제 수단 선택 요청을 처리하는 API
     * <br/>
     * 이 API는 결제 흐름 중 <b>10-12번째 단계</b>에서 사용됩니다.
     * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
     *
     * @param request 결제 수단 업데이트 요청 정보
     * @return 결제 UI로 리다이렉트할 URL 정보
     */
    @PostMapping("/method")
    public ResponseEntity<BaseResponse<PaymentMethodUpdateResponseDto>> updatePaymentMethod(
            @RequestBody PaymentMethodUpdateRequestDto request) {

        log.info("결제 수단 선택 요청 수신: paymentId={}, method={}",
                 request.getPaymentId(), request.getMethod());

        PaymentMethodUpdateResponseDto response = paymentService.updatePaymentMethod(request);

        log.info("결제 UI URL 생성 완료: redirectUrl={}", response.getRedirectUrl());

        return ResponseEntity.ok(BaseResponse.success(response));
    }


    /**
     * 앱카드 서버로부터 결제 인증 결과를 받아 승인을 처리하는 API
     * <br/>
     * 이 API는 결제 흐름 중 <b>32번째 단계</b>에서 사용됩니다.
     * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
     *
     * @param request 결제 인증 결과 및 승인 요청 정보
     * @return 결제 승인 결과
     */
    @PostMapping("/appcard/approval")
    public ResponseEntity<BaseResponse<Void>> processAppCardPaymentApproval(
            @RequestBody AppCardPaymentAuthResultDto request) {

        log.info("앱카드 결제 승인 요청 수신: 트랜잭션ID={}, 인증여부={}", request.getTxnId(), request.isAuthenticated());

        Object response = paymentService.processPaymentApproval(request);

        // 인증 실패 경우
        if (response instanceof AppCardPaymentAuthFailDto) {
            AppCardPaymentAuthFailDto failResponse = (AppCardPaymentAuthFailDto) response;
            log.warn("인증 실패: 트랜잭션ID={}, 상태={}", failResponse.getTxnId(), failResponse.getPaymentStatus());
            return ResponseEntity.ok(BaseResponse.fail("AUTHENTICATION_FAILED"));
        }

        // 카드 승인 응답인 경우
        if (response instanceof CardPaymentApprovalResponseDto) {
            CardPaymentApprovalResponseDto approvalResponse = (CardPaymentApprovalResponseDto) response;

            // 승인 실패 시 적절한 HTTP 상태 코드 반환
            if (approvalResponse.getPaymentStatus() != PaymentStatus.SUCCEEDED) {
                log.warn("결제 승인 실패: 트랜잭션ID={}, 상태={}", approvalResponse.getTransactionId(), approvalResponse.getPaymentStatus());
                return ResponseEntity.ok(BaseResponse.fail(approvalResponse.getPaymentStatus().name()));
            }

            return ResponseEntity.ok(BaseResponse.success());
        }

        // 예상치 못한 응답 타입
        log.error("예상치 못한 응답 타입: {}", response.getClass().getName());
        return ResponseEntity.ok(BaseResponse.fail("UNEXPECTED_RESPONSE_TYPE"));
    }


    /**
     * 결제 검증 요청을 처리하는 API
     * 결제 흐름 중 44-46번째 단계에서 사용
     *
     * @param request 결제 검증 요청 정보
     * @return 결제 검증 결과
     */
    @PostMapping("/verify")
    public ResponseEntity<BaseResponse<PaymentVerificationResponseDto>> verifyPayment(
            @RequestBody PaymentVerificationRequestDto request) {

        log.info("결제 검증 요청 수신: paymentId={}, orderId={}, merchantId={}",
                request.getPaymentId(), request.getOrderId(), request.getMerchantId());

        PaymentVerificationResponseDto response = paymentService.verifyPayment(request);

        log.info("결제 검증 완료: paymentId={}, 검증결과={}",
                response.getPaymentId(), response.isVerified());

        return ResponseEntity.ok(BaseResponse.success(response));
    }
}