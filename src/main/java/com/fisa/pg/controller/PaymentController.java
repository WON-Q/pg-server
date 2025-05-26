package com.fisa.pg.controller;

import com.fisa.pg.dto.request.AppCardPaymentRequestDto;
import com.fisa.pg.dto.request.PaymentCreateRequestDto;
import com.fisa.pg.dto.request.PaymentMethodUpdateRequestDto;
import com.fisa.pg.dto.response.BaseResponse;
import com.fisa.pg.dto.response.PaymentCreateResponseDto;
import com.fisa.pg.dto.response.PaymentMethodUpdateResponseDto;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.feign.dto.card.response.CardPaymentApprovalResponseDto;
import com.fisa.pg.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 가맹점에서 결제를 생성하기 위한 API
     * <ul>
     *     <li>결제 생성 요청 (결제 흐름 중 <b>5번째 단계</b>)</li>
     *     <li>결제 생성 응답 (결제 흐름 중 <b>7번째 단계</b>)</li>
     * </ul>
     * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
     *
     * @param request 결제 생성 요청 정보
     * @return 결제 생성 응답 정보
     */
    @PostMapping("/prepare")
    public ResponseEntity<BaseResponse<PaymentCreateResponseDto>> createPayment(
            @RequestBody PaymentCreateRequestDto request,
            @AuthenticationPrincipal Merchant merchant
    ) {
        PaymentCreateResponseDto data = paymentService.createPayment(request, merchant);
        return ResponseEntity.ok(BaseResponse.onCreate("결제가 생성되었습니다.", data));
    }

    /**
     * 결제 수단 선택 요청 API
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

        return ResponseEntity.ok(BaseResponse.onSuccess("결제 수단 선택이 완료되었습니다.", response));
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
    @PostMapping("/payments/authorize")
    public ResponseEntity<BaseResponse<CardPaymentApprovalResponseDto>> processAppCardPaymentApproval(
            @RequestBody AppCardPaymentRequestDto request
    ) {

        log.info("앱카드 결제 승인 요청 수신: 트랜잭션ID={}, 인증여부={}", request.getTxnId(), request.isAuthenticated());

        CardPaymentApprovalResponseDto data = paymentService.processPaymentApproval(request);
        return ResponseEntity.ok(BaseResponse.onSuccess("결제 승인 요청이 완료되었습니다.", data));
    }

}
