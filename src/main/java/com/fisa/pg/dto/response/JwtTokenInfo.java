package com.fisa.pg.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class JwtTokenInfo {

    private final String accessToken;
    private final LocalDateTime accessTokenExpiresAt;
    private final String refreshToken;
    private final LocalDateTime refreshTokenExpiresAt;

    public static JwtTokenInfo from(String accessToken, LocalDateTime accessExp,
                                    String refreshToken, LocalDateTime refreshExp) {
        return JwtTokenInfo.builder()
                .accessToken(accessToken)
                .accessTokenExpiresAt(accessExp)
                .refreshToken(refreshToken)
                .refreshTokenExpiresAt(refreshExp)
                .build();
    }
}