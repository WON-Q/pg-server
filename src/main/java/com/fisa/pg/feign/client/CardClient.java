package com.fisa.pg.feign.client;

import com.fisa.pg.config.feign.CardClientConfig;
import com.fisa.pg.feign.dto.card.request.CardPaymentApprovalRequestDto;
import com.fisa.pg.feign.dto.card.response.BaseResponse;
import com.fisa.pg.feign.dto.card.response.CardPaymentApprovalResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "cardClient", url = "${app.card.endpoint}", configuration = CardClientConfig.class)
public interface CardClient {

    /**
     * 카드사 결제 요청 API
     * <ul>
     *     <li>결제 승인 요청 (결제 흐름 중 <b>33번째 단계</b>)</li>
     *     <li>결제 승인 응답 (결제 흐름 중 <b>36번째 단계</b>)</li>
     * </ul>
     * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
     *
     * @param request 결제 승인 요청 정보
     * @return 결제 승인 응답 정보
     */
    @PostMapping("/api/v1/card/payment/authorization")
    BaseResponse<CardPaymentApprovalResponseDto> requestAuth(
            @RequestBody CardPaymentApprovalRequestDto request
    );

}
