package com.fisa.pg.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PG 대시보드용 클라이언트 로그인 응답 DTO
 * <br/>
 * 이 DTO는 로그인 성공 시 클라이언트에게 반환되는 데이터 구조를 정의합니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

    /**
     * 사용자 ID
     */
    private Long id;

    /**
     * 사용자 이름
     */
    private String name;

    /**
     * 사용자 이메일
     */
    private String email;

    /**
     * 사용자 토큰
     */
    private TokenDto token;

}