package com.fisa.pg.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fisa.pg.entity.user.Role;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {

    private String tokenType;

    private Role role;

    private String accessToken;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date accessTokenExpiresAt;

    private String refreshToken;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date refreshTokenExpiresAt;

}
