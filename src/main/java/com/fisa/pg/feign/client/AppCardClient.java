package com.fisa.pg.feign.client;

import com.fisa.pg.config.feign.AppCardClientConfig;
import com.fisa.pg.dto.BaseResponse;
import com.fisa.pg.feign.dto.appcard.request.AppCardAuthRequestDto;
import com.fisa.pg.feign.dto.appcard.request.AppCardPaymentResultDto;
import com.fisa.pg.feign.dto.appcard.response.AppCardAuthResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "appCardClient", url = "${app.appcard.endpoint:localhost:8083}", configuration = AppCardClientConfig.class)
public interface AppCardClient {

    /**
     * 앱카드 인증 요청 API
     * <br/>
     * 이 API는 결제 흐름 중 <b>16번째 단계</b>에서 사용됩니다.
     * PG사에서 앱카드 서버로 인증을 요청합니다.
     * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
     *
     * @param request 앱카드 인증 요청 정보
     * @return 앱카드 인증 응답
     */
    @PostMapping("/auth")
    ResponseEntity<AppCardAuthResponseDto> requestAppCardAuth(
            @RequestBody AppCardAuthRequestDto request
    );

    /**
     * 결제 결과 전송
     *
     * @param result 결제 결과 DTO
     * @return 결과 전송 응답
     */
    @PostMapping("/api/payment/result")
    ResponseEntity<BaseResponse<Void>> sendPaymentResult(@RequestBody AppCardPaymentResultDto result);
}