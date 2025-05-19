package com.fisa.pg.controller;

import com.fisa.pg.dto.request.CreateWebhookRequestDto;
import com.fisa.pg.dto.response.BaseResponse;
import com.fisa.pg.dto.response.CreateWebhookResponseDto;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 가맹점 웹훅 관련 API
 * <br/>
 * 가맹점의 웹훅을 생성하는 API를 제공합니다.
 */
@RestController
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
public class MerchantWebhookController {

    private final WebhookService webhookService;

    /**
     * 가맹점 웹훅 생성 API
     * <br/>
     * 가맹점의 웹훅을 생성합니다.
     *
     * @param merchant   웹훅을 생성할 가맹점
     * @param requestDto 웹훅 요청 DTO
     * @return 웹훅 응답 DTO
     */
    @PostMapping("/webhook")
    public ResponseEntity<BaseResponse<CreateWebhookResponseDto>> createWebhook(
            @AuthenticationPrincipal Merchant merchant,
            @RequestBody CreateWebhookRequestDto requestDto
    ) {
        CreateWebhookResponseDto response = webhookService.createWebhook(merchant.getId(), requestDto);
        return ResponseEntity.ok(BaseResponse.onSuccess("가맹점 웹훅 생성 성공", response));
    }

    /**
     * 가맹점 웹훅 조회 API
     * <br/>
     * 가맹점의 웹훅 설정 정보를 조회합니다.
     *
     * @param merchant 로그인한 가맹점
     * @return 웹훅 응답 DTO
     */
    @GetMapping("/webhook")
    public ResponseEntity<BaseResponse<CreateWebhookResponseDto>> getWebhook(
            @AuthenticationPrincipal Merchant merchant
    ) {
        CreateWebhookResponseDto response = CreateWebhookResponseDto.from(merchant);
        return ResponseEntity.ok(BaseResponse.onSuccess("가맹점 웹훅 조회 성공", response));
    }
}
