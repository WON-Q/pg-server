package com.fisa.pg.feign.client;

import com.fisa.pg.config.feign.AppCardClientConfig;
import com.fisa.pg.feign.dto.appcard.request.AppCardAuthRequestDto;
import com.fisa.pg.feign.dto.appcard.response.AppCardAuthResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "appCardClient", url = "${app.appcard.endpoint}", configuration = AppCardClientConfig.class)
public interface AppCardClient {

    /**
     * 앱카드 결제 인증 요청 API
     * <ul>
     *     <li>결제 인증 요청 (결제 흐름 중 <b>16번째 단계</b>)</li>
     *     <li>딥링크 생성 및 반환 (결제 흐름 중 <b>19번째 단계</b>)</li>
     * </ul>
     * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
     *
     * @param request 결제 인증 요청 정보
     * @return 앱카드 인증 응답 (딥링크 포함)
     */
    @PostMapping("/authentication")
    AppCardAuthResponseDto requestAuth(
            @RequestBody AppCardAuthRequestDto request
    );

}
