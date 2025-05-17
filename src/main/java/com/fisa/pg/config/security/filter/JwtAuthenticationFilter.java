package com.fisa.pg.config.security.filter;

import com.fisa.pg.entity.user.Admin;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.entity.user.Role;
import com.fisa.pg.repository.AdminRepository;
import com.fisa.pg.repository.MerchantRepository;
import com.fisa.pg.utils.JwtUtil;
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
import java.util.Collections;

/**
 * JWT 토큰을 검증하고 사용자 인증 정보를 설정하는 필터
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final MerchantRepository merchantRepository;

    private final AdminRepository adminRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 요청 헤더에서 JWT 토큰 추출
            String token = extractToken(request);

            // 토큰이 유효한 경우 인증 정보 설정
            if (token != null && jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                Role userRole = jwtUtil.getUserRoleFromToken(token);

                // 사용자 정보 조회 및 인증 설정
                if (userRole == Role.MERCHANT) {
                    merchantRepository.findById(userId).ifPresent(user -> authenticateUser(user, userRole));
                } else if (userRole == Role.ADMIN) {
                    adminRepository.findById(userId).ifPresent(user -> authenticateUser(user, userRole));
                } else {
                    log.warn("알 수 없는 역할: {}", userRole);
                }
            }

        } catch (Exception e) {
            log.error("JWT 인증 처리 중 오류 발생: {}", e.getMessage());
            // 인증 오류가 발생해도 필터 체인은 계속 진행 (인증되지 않은 상태로)
        }

        filterChain.doFilter(request, response);
    }

    /**
     * HttpServletRequest에서 Authorization 헤더의 Bearer 토큰을 추출하는 메서드
     *
     * @param request HTTP 요청 객체
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    /**
     * 사용자 인증 정보를 SecurityContext에 설정하는 메서드
     *
     * @param user 사용자 객체
     * @param role 사용자 역할
     */
    private void authenticateUser(Object user, Role role) {
        // 사용자 인증 정보 설정
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.singleton(new SimpleGrantedAuthority(role.name())));

        // SecurityContext에 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 로그인 성공 시 사용자 ID와 역할을 로그에 기록
        log.debug("{} '{}' 인증 성공", role.name(), getUserId(user));
    }

    /**
     * 사용자 ID를 추출하는 메서드
     *
     * @param user 사용자 객체
     * @return 사용자 ID
     */
    private Long getUserId(Object user) {
        if (user instanceof Merchant merchant) return merchant.getId();
        if (user instanceof Admin admin) return admin.getId();
        return null;
    }

}
