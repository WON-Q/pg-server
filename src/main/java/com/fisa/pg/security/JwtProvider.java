package com.fisa.pg.security;

import com.fisa.pg.dto.request.MerchantJwtIssueRequestDto;
import com.fisa.pg.dto.response.MerchantJwtResponseDto;
import com.fisa.pg.entity.user.Merchant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 토큰 관련 기능 제공
 * <p>
 * 가맹점 API 인증을 위한 JWT 토큰 발행 및 검증 처리
 * </p>
 */
@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final Key key;

    public JwtProvider(@Value("${jwt.secret:defaultSecretKeyForDevelopment}") String jwtSecret) {
        // HS512 알고리즘을 사용하기 위한 최소 64바이트 키 생성
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * 가맹점 API 키 발급
     *
     * @param merchant 가맹점 정보
     * @param request  API 키 발급 요청 정보
     * @return 발급된 JWT 응답 객체
     */
    public MerchantJwtResponseDto generateMerchantApiKey(Merchant merchant, MerchantJwtIssueRequestDto request) {
        // API 키 이름
        String keyName = request.getName();

        // 유효기간 설정
        LocalDate today = LocalDate.now();
        LocalDate expiryDate = today.plusDays(request.getValidDays());

        // 공개 키(accessKey)는 가맹점 ID + 랜덤 UUID 조합
        String accessKey = "MID" + merchant.getId() + "_" + UUID.randomUUID().toString().substring(0, 8);

        // 비밀 키(secretKey)는 무작위 UUID
        String secretKey = UUID.randomUUID().toString();

        // JWT 토큰 생성 (실제로는 사용 시점에 생성)
        String token = Jwts.builder()
                .setSubject(merchant.getId().toString())
                .claim("merchantId", merchant.getId())
                .claim("businessNumber", merchant.getBusinessNumber())
                .claim("keyName", keyName)
                .claim("accessKey", accessKey)
                .setIssuedAt(Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expiryDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        // 응답 DTO 구성
        return MerchantJwtResponseDto.builder()
                .name(keyName)
                .accessKey(accessKey)
                .secretKey(secretKey) // 보안을 위해 1회만 표시하고 다시 조회 불가
                .validFrom(today.toString())
                .validTo(expiryDate.toString())
                .build();
    }

    /**
     * JWT 토큰 검증
     *
     * @param token 검증할 JWT 토큰
     * @return 토큰 유효성 여부
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * JWT 토큰에서 가맹점 ID 추출
     *
     * @param token JWT 토큰
     * @return 가맹점 ID
     */
    public Long getMerchantIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("merchantId", Long.class);
    }

    /**
     * 실제 API 요청 시 AccessKey와 SecretKey로 JWT 생성
     *
     * @param merchantId 가맹점 ID
     * @param accessKey 액세스 키
     * @return 생성된 JWT 토큰
     */
    public String createJwtToken(Long merchantId, String accessKey) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 3600000); // 1시간 유효

        return Jwts.builder()
                .setSubject(merchantId.toString())
                .claim("merchantId", merchantId)
                .claim("accessKey", accessKey)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}