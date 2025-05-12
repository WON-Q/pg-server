package com.fisa.pg.controller;

import com.fisa.pg.dto.request.WebhookUrlRequestDto;
import com.fisa.pg.dto.response.WebhookUrlResponseDto;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.service.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 가맹점 전용 Webhook URL 관리 컨트롤러
 */
@RestController
@RequestMapping("/api/merchants/me/webhook-url")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    /**
     * Webhook URL 등록 또는 수정
     */
    @PatchMapping
    public void updateWebhookUrl(
            @AuthenticationPrincipal Merchant merchant,
            @RequestBody @Valid WebhookUrlRequestDto dto
    ) {
        merchantService.updateWebhookUrl(merchant, dto);
    }

    /**
     * Webhook URL 조회
     */
    @GetMapping
    public WebhookUrlResponseDto getWebhookUrl(
            @AuthenticationPrincipal Merchant merchant
    ) {
        return merchantService.getWebhookUrl(merchant);
    }

    /**
     * Webhook URL 삭제
     */
    @DeleteMapping
    public void deleteWebhookUrl(
            @AuthenticationPrincipal Merchant merchant
    ) {
        merchantService.deleteWebhookUrl(merchant);
    }
}
