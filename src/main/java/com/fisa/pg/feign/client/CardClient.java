package com.fisa.pg.feign.client;

import com.fisa.pg.config.feign.CardClientConfig;
import com.fisa.pg.feign.dto.card.request.CardPaymentApprovalRequestDto;
import com.fisa.pg.feign.dto.card.request.CardRefundRequestDto;
import com.fisa.pg.feign.dto.card.response.CardPaymentApprovalResponseDto;
import com.fisa.pg.feign.dto.card.response.CardRefundResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 카드사 API를 호출하기 위한 Feign 클라이언트
 */
@FeignClient(name = "cardClient", url = "${app.card.endpoint:localhost:8082}", configuration = CardClientConfig.class)
public interface CardClient {

    /**
     * 카드 결제 승인 요청 API
     * <br/>
     * 이 API는 결제 흐름 중 <b>33번째 단계</b>에서 사용됩니다.
     * PG사에서 카드사로 결제 승인을 요청합니다.
     * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
     *
     * @param request 카드 결제 승인 요청 정보
     * @return 카드 결제 승인 응답
     */
    @PostMapping("/approval")
    CardPaymentApprovalResponseDto requestCardApproval(
            @RequestBody CardPaymentApprovalRequestDto request
    );

    @PostMapping("/refund")
    CardRefundResponseDto requestCardRefund(@RequestBody CardRefundRequestDto request);
}