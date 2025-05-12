package com.fisa.pg.controller;

import com.fisa.pg.dto.response.MerchantJwtResponseDto;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.service.MerchantApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/merchant/keys")
@RequiredArgsConstructor
public class MerchantApiKeyController {

    private final MerchantApiKeyService merchantApiKeyService;

    /**
     * API 키 발급 요청
     *
     * @param name 키 이름
     * @param validDays 유효 기간 (일)
     * @param merchant 로그인한 가맹점 (보통 SecurityContext나 ArgumentResolver로 주입)
     * @return 발급된 키 정보
     */
    @PostMapping("/issue")
    public MerchantJwtResponseDto issueApiKey(
            @RequestParam String name,
            @RequestParam(defaultValue = "30") int validDays,
            @RequestAttribute("merchant") Merchant merchant // 인증된 가맹점 주입
    ) {
        return merchantApiKeyService.issueApiKey(merchant, name, validDays);
    }
}
