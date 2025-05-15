package com.fisa.pg.config.security.filter;

import com.fisa.pg.entity.auth.ApiKey;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;

/**
 * Basic Authentication 토큰을 검증하고 사용자 인증 정보를 설정하는 필터
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BasicAuthenticationFilter extends OncePerRequestFilter {

    private final ApiKeyRepository apiKeyRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 요청 헤더에서 Basic 토큰 추출
            String[] credentials = extractCredentials(request);

            // 자격 증명이 유효한 경우 인증 정보 설정
            if (credentials != null) {
                String accessKey = credentials[0];
                String secretKey = credentials[1];

                // ApiKey 정보 조회
                apiKeyRepository.findByAccessKey(accessKey).ifPresent(apiKey -> {
                    if (isValidApiKey(apiKey) && validateSecretKey(apiKey, secretKey)) {
                        Merchant merchant = apiKey.getMerchant();

                        if (merchant.isActive()) {
                            // 인증 정보 생성 (역할은 기본적으로 ROLE_MERCHANT로 설정)
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    merchant,
                                    null,
                                    Collections.singleton(new SimpleGrantedAuthority("ROLE_MERCHANT"))
                            );

                            // SecurityContextHolder에 인증 정보 설정
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            log.debug("가맹점 '{}' 인증 성공", merchant.getId());

                        } else {
                            log.warn("비활성화된 가맹점으로 인증 시도: accessKey={}", accessKey);
                        }

                    } else {
                        log.warn("잘못된 secret key 또는 유효하지 않은 API 키로 인증 시도: accessKey={}", accessKey);
                    }
                });
            }

        } catch (Exception e) {
            log.error("Basic 인증 처리 중 오류 발생: {}", e.getMessage());
            // 인증 오류가 발생해도 필터 체인은 계속 진행 (인증되지 않은 상태로)
        }

        filterChain.doFilter(request, response);
    }

    /**
     * HttpServletRequest에서 Authorization 헤더의 Basic 토큰을 추출하고 디코딩하는 메서드
     *
     * @param request HTTP 요청 객체
     * @return String[] {accessKey, secretKey} 또는 null
     */
    private String[] extractCredentials(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Basic ")) {
            String base64Credentials = authHeader.substring(6);
            try {
                // Base64 디코딩
                String credentials = new String(Base64.getDecoder().decode(base64Credentials));

                // accessKey:secretKey 형식으로 분리
                String[] parts = credentials.split(":", 2);
                if (parts.length == 2) {
                    return parts;
                }

            } catch (IllegalArgumentException e) {
                log.error("Base64 디코딩 실패: {}", e.getMessage());
            }
        }

        return null;
    }

    /**
     * ApiKey 객체의 secret key와 제공된 secret key를 검증하는 메서드
     *
     * @param apiKey    ApiKey 객체
     * @param secretKey 제공된 secret key
     * @return 검증 결과
     */
    private boolean validateSecretKey(ApiKey apiKey, String secretKey) {
        return apiKey.getSecretKey().equals(secretKey);
    }

    /**
     * Api Key가 유효한지 확인하는 메서드
     *
     * @param apiKey Api Key
     * @return Api Key의 유효 여부
     */
    private boolean isValidApiKey(ApiKey apiKey) {
        if (!apiKey.isActive()) {
            return false;
        }

        LocalDateTime expiresAt = apiKey.getExpiresAt();
        return expiresAt == null || expiresAt.isAfter(LocalDateTime.now());
    }
}