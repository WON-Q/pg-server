package com.fisa.pg.feign.client;

import com.fisa.pg.config.feign.WonQClientConfig;
import com.fisa.pg.feign.dto.wonq.request.DeepLinkPaymentRequestDto;
import com.fisa.pg.feign.dto.wonq.response.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "wonqClient", url = "${app.wonq.endpoint}", configuration = WonQClientConfig.class)
public interface WonQClient {

    /**
     * 원큐 오더 서버에 딥링크 정보를 전송하는 API
     * <br/>
     * 이 API는 결제 흐름 중 <b>20번째 단계</b>에서 사용됩니다.
     * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
     *
     * @param request 딥링크 정보가 포함된 요청
     * @return 응답
     */
    @PostMapping("/deeplink")
    ResponseEntity<BaseResponse<Void>> sendDeepLink(
            @RequestBody DeepLinkPaymentRequestDto request
    );

}
