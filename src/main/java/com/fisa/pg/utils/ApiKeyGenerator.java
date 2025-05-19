package com.fisa.pg.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * API Key 생성 유틸리티 클래스
 */
@Component
public final class ApiKeyGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    /**
     * 인스턴스 생성 방지용 private 생성자
     */
    private ApiKeyGenerator() {

    }

    /**
     * Access Key 생성하는 메서드 (24바이트 랜덤 데이터)
     */
    public static String generateAccessKey() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    /**
     * Secret Key 생성하는 메서드 (48바이트 랜덤 데이터)
     */
    public static String generateSecretKey() {
        byte[] randomBytes = new byte[48];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}