package com.fisa.pg.dto.response;

import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.entity.user.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LoginResponseDto {

    private LocalDateTime lastLoginAt;
    private Role role;
    private String tokenType;
    private String accessToken;
    private LocalDateTime accessTokenExpiresAt;
    private String refreshToken;
    private LocalDateTime refreshTokenExpiresAt;

    public static LoginResponseDto from(Merchant merchant, JwtTokenInfo tokenInfo) {
        return LoginResponseDto.builder()
                .lastLoginAt(merchant.getLastLoginAt())
                .role(merchant.getRole())
                .tokenType("Bearer")
                .accessToken(tokenInfo.getAccessToken())
                .accessTokenExpiresAt(tokenInfo.getAccessTokenExpiresAt())
                .refreshToken(tokenInfo.getRefreshToken())
                .refreshTokenExpiresAt(tokenInfo.getRefreshTokenExpiresAt())
                .build();
    }
}