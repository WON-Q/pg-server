package com.fisa.pg.config.feign;

import feign.Logger;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 외부 API 통신을 위한 Feign Client를 활성화하는 설정 클래스
 */
@Configuration
@EnableFeignClients(basePackages = "com.fisa.pg.feign")
public class FeignConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

}
