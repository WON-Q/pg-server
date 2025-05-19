package com.fisa.pg.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PG 대시보드용 클라이언트 로그인 요청 DTO
 * <br />
 * 이 DTO는 로그인 요청 시 클라이언트가 서버에 전송하는 데이터 구조를 정의합니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    /**
     * 사용자 아이디
     */
    private String id;

    /**
     * 사용자 비밀번호
     */
    private String password;

}