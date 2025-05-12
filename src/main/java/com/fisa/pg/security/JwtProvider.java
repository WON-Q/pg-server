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

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

    private final SecretKey key;

    public JwtProvider(@Value("${jwt.secret:}") String configuredSecret) {
        // Generate a secure key for HS512
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    /**
     * 가맹점 API 키 발급
     */
    public MerchantJwtResponseDto generateMerchantApiKey(Merchant merchant, MerchantJwtIssueRequestDto request) {
        String keyName = request.getName();
        LocalDate today = LocalDate.now();
        LocalDate expiryDate = today.plusDays(request.getValidDays());

        String accessKey = "MID" + merchant.getId() + "_" + UUID.randomUUID().toString().substring(0, 8);
        String secretKey = UUID.randomUUID().toString();

        String token = Jwts.builder()
                .setSubject(merchant.getId().toString())
                .claim("merchantId", merchant.getId())
                .claim("businessNumber", merchant.getBusinessNumber())
                .claim("keyName", keyName)
                .claim("accessKey", accessKey)
                .setIssuedAt(Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expiryDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .signWith(key)
                .compact();

        return MerchantJwtResponseDto.builder()
                .name(keyName)
                .accessKey(accessKey)
                .secretKey(secretKey)
                .validFrom(today.toString())
                .validTo(expiryDate.toString())
                .build();
    }

    /**
     * JWT 토큰 검증
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
                .signWith(key)
                .compact();
    }
}