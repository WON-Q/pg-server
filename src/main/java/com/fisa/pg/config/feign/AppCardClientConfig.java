package com.fisa.pg.config.feign;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * 앱 카드사 API 연동을 위한 Feign Client 설정 클래스
 */
public class AppCardClientConfig {

    @Value("${app.appcard.token}")
    private String token;

    /**
     * 앱 카드사 API 호출 시 필요한 헤더를 설정하는 RequestInterceptor Bean
     *
     * @return RequestInterceptor
     */
    @Bean
    public RequestInterceptor appCardRequestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Authorization", "Bearer " + token); // 헤더에 Authorization 추가
            requestTemplate.header("Content-Type", "application/json"); // Content-Type 헤더 추가
        };
    }

}
