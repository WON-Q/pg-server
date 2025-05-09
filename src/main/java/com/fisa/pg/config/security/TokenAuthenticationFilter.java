package com.fisa.pg.config.security;

import com.fisa.pg.entity.auth.ApiToken;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.repository.ApiTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

/**
 * 인증 필터로, HTTP 요청의 Authorization 헤더에서 Bearer 토큰을 추출하고,
 * 유효한 API 토큰(PG_로 시작하는 경우)에 대해 인증 처리를 수행합니다.
 * <br/>
 * 이 필터는 Spring Security의 OncePerRequestFilter를 상속하여
 * 요청당 한 번만 실행됩니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private static final String TOKEN_PREFIX = "PG_";

    private final ApiTokenRepository apiTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);

            if (token != null) {
                processApiTokenAuthentication(request, token);
            }

        } catch (Exception e) {
            log.error("토큰 인증 중 오류 발생: ", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Authorization 헤더에서 Bearer 토큰을 추출하는 메서드
     *
     * @param request HTTP 요청 객체
     * @return 추출된 Bearer 토큰 값
     */
    private String extractToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization")) // Authorization 헤더 가져오기
                .filter(header -> header.startsWith(BEARER_PREFIX)) // Bearer로 시작하는지 확인
                .map(header -> header.substring(BEARER_PREFIX.length())) // Bearer 접두사 제거
                .filter(token -> token.startsWith(TOKEN_PREFIX))  // PG_로 시작하는지 확인
                .orElse(null); // 없으면 null 반환
    }

    private void processApiTokenAuthentication(HttpServletRequest request, String tokenValue) {
        // DB에서 토큰 조회
        Optional<ApiToken> optionalToken = apiTokenRepository.findByTokenValueAndIsActiveTrue(tokenValue);

        //사용자가 보낸 요청에 들어있는 토큰이 DB에 존재하는 경우
        optionalToken.ifPresent(token -> {
            // 토큰 만료 확인
            if (token.getExpiresAt().isAfter(LocalDateTime.now())) {
                Merchant merchant = token.getMerchant();

                // 가맹점이 활성 상태인지 확인
                if (merchant.isActive()) {
                    // 인증 객체 생성
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            merchant,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + merchant.getRole().name()))
                    );

                    // 인증 세부 정보 설정
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Spring Security의 SecurityContext에 인증 정보 설정
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        });
    }
}