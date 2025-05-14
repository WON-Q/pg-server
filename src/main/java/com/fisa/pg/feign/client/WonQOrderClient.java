package com.fisa.pg.feign.client;

import com.fisa.pg.dto.BaseResponse;
import com.fisa.pg.config.feign.WonQOrderClientConfig;
import com.fisa.pg.feign.dto.wonq.request.DeepLinkPaymentRequestDto;
import com.fisa.pg.feign.dto.wonq.request.PaymentResultNotificationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "wonQOrderClient", url = "${app.wonq.endpoint}", configuration = WonQOrderClientConfig.class)
public interface WonQOrderClient {

    /**
     * 원큐 오더 서버에 딥링크 정보를 전송하는 API
     * <br/>
     * 이 API는 결제 흐름 중 <b>20번째 단계</b>에서 사용됩니다.
     * PG사에서 원큐 오더 서버로 딥링크 정보를 전달합니다.
     * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
     *
     * @param request 딥링크 정보가 포함된 요청
     * @return 응답
     */
    @PostMapping("/deeplink")
    ResponseEntity<BaseResponse<Void>> sendDeepLink(
            @RequestBody DeepLinkPaymentRequestDto request
    );

    /**
     * 원큐 오더 서버에 결제 결과를 전송하는 API
     * 결제 흐름 중 38번째 단계에서 사용
     *
     * @param request 결제 결과 DTO
     * @return 응답 결과
     */
    @PostMapping("/payments/result")
    ResponseEntity<BaseResponse<Void>> sendPaymentResult(
            @RequestBody PaymentResultNotificationDto request
    );
}