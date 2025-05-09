package com.fisa.pg.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fisa.pg.entity.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 로그인 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

    /**
     * 마지막 로그인 시각
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginAt;

    /**
     * 로그인한 사용자의 Role
     */
    private Role role;

    /**
     * 발급해준 토큰의 타입
     */
    private String tokenType;

    /**
     * 액세스 토큰
     */
    private String accessToken;

    /**
     * 액세스 토큰 만료 시각
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime accessTokenExpiresAt;

    /**
     * 리프레시 토큰
     */
    private String refreshToken;

    /**
     * 리프레시 토큰 만료 시각
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refreshTokenExpiresAt;

}