package com.fisa.pg.config.feign;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 원큐 오더 서버 API 연동을 위한 Feign Client 설정 클래스
 */
@Configuration
public class WonQOrderClientConfig {

    @Value("${app.wonq.token}")
    private String token;

    /**
     * 원큐 오더 서버 API 호출 시 필요한 헤더를 설정하는 RequestInterceptor Bean
     *
     * @return RequestInterceptor
     */
    @Bean
    public RequestInterceptor wonQOrderRequestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Authorization", "Bearer " + token);
            requestTemplate.header("Content-Type", "application/json");
        };
    }
}