package com.fisa.pg.controller;

import com.fisa.pg.dto.request.PaymentCreateRequestDto;
import com.fisa.pg.dto.response.BaseResponse;
import com.fisa.pg.dto.response.PaymentCreateResponseDto;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

}
