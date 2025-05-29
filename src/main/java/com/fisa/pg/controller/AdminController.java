package com.fisa.pg.controller;

import com.fisa.pg.dto.response.ApiKeyResponseDto;
import com.fisa.pg.dto.response.BaseResponse;
import com.fisa.pg.dto.response.WebhookResponseDto;
import com.fisa.pg.service.ApiKeyService;
import com.fisa.pg.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자 전용 컨트롤러
 * <p>
 * 이 컨트롤러는 관리자 전용 API를 제공합니다.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final ApiKeyService apiKeyService;

    private final WebhookService webhookService;

    /**
     * 전체 API 키 목록 페이징 조회 API
     *
     * @param pageable 페이지 번호, 페이지 크기, 정렬 조건 등의 페이징 정보
     * @return 페이징된 API 키 목록과 함께 200 OK 응답
     * @throws IllegalArgumentException 페이징 파라미터가 유효하지 않은 경우 발생
     */
    @GetMapping("/api-keys")
    public ResponseEntity<BaseResponse<Page<ApiKeyResponseDto>>> getApiKeyList(Pageable pageable) {
        Page<ApiKeyResponseDto> data = apiKeyService.getApiKeyList(pageable);
        return ResponseEntity.ok(BaseResponse.onSuccess("API 키 목록 조회 성공", data));
    }

    /**
     * 관리자용 웹훅 목록 조회 API
     *
     * @param pageable 페이징 정보
     * @return 웹훅 응답 DTO 페이지
     */
    @GetMapping("/webhooks")
    public ResponseEntity<BaseResponse<Page<WebhookResponseDto>>> getAllWebhooks(Pageable pageable) {
        Page<WebhookResponseDto> data = webhookService.getWebhookList(pageable);
        return ResponseEntity.ok(BaseResponse.onSuccess("모든 웹훅 조회 성공", data));
    }
}