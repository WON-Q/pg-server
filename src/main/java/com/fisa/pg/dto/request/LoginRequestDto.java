package com.fisa.pg.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 가맹점 로그인 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    /**
     * 가맹점 이메일 (로그인 ID로 사용)
     */
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    private String email;

    /**
     * 가맹점 비밀번호
     */
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

}