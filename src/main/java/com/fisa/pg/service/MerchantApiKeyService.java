package com.fisa.pg.service;

import com.fisa.pg.dto.request.MerchantJwtIssueRequestDto;
import com.fisa.pg.dto.response.MerchantJwtResponseDto;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 가맹점 API 키 발급 서비스
 */
@Service
@RequiredArgsConstructor
public class MerchantApiKeyService {

    private final JwtProvider jwtProvider;

    @Transactional
    public MerchantJwtResponseDto issueApiKey(Merchant merchant, String name, int validDays) {
        MerchantJwtIssueRequestDto request = MerchantJwtIssueRequestDto.of(name, validDays);
        return jwtProvider.generateMerchantApiKey(merchant, request);
    }
}
