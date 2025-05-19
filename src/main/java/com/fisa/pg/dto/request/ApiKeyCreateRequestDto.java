package com.fisa.pg.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fisa.pg.dto.response.ApiKeyCreatedResponseDto;
import com.fisa.pg.entity.auth.ApiKey;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ApiKeyCreateRequestDto {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "액세스 날짜는 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiresAt;

}
