package com.fisa.pg.security;

import com.fisa.pg.dto.response.JwtTokenInfo;
import com.fisa.pg.entity.user.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public JwtTokenInfo generateToken(Long merchantId, String email, Role role) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime accessExp = now.plusHours(1);
        LocalDateTime refreshExp = now.plusDays(7);

        String accessToken = Jwts.builder()
                .setSubject(email)
                .claim("merchantId", merchantId)
                .claim("role", role.name())
                .setIssuedAt(toDate(now))
                .setExpiration(toDate(accessExp))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(toDate(now))
                .setExpiration(toDate(refreshExp))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return JwtTokenInfo.from(accessToken, accessExp, refreshToken, refreshExp);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Date toDate(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }
}
