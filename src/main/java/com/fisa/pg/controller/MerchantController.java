package com.fisa.pg.controller;

import com.fisa.pg.dto.request.CreateApiKeyRequestDto;
import com.fisa.pg.dto.request.CreateWebhookRequestDto;
import com.fisa.pg.dto.response.ApiKeyResponseDto;
import com.fisa.pg.dto.response.BaseResponse;
import com.fisa.pg.dto.response.CreateApiKeyResponseDto;
import com.fisa.pg.dto.response.CreateWebhookResponseDto;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.service.ApiKeyService;
import com.fisa.pg.service.WebhookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 가맹점용 컨트롤러
 * <p>
 * 가맹점은 인증된 상태에서만 이 API를 사용할 수 있습니다.
 * </p>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/merchants")
public class MerchantController {

    private final ApiKeyService apiKeyService;

    private final WebhookService webhookService;

    /**
     * 가맹점용 API 키 발급 엔드포인트
     * <p>
     * 가맹점이 새로운 API 키를 생성할 수 있는 엔드포인트입니다.
     * 이름과 만료일을 지정하여 API 키를 생성할 수 있으며,
     * 만료일이 지정되지 않은 경우 기본값으로 1년 후로 설정됩니다.
     * </p>
     *
     * @param requestDto API 키 생성 요청 정보 (이름, 만료일)
     * @param merchant   현재 인증된 가맹점 정보
     * @return 생성된 API 키 정보와 함께 201 Created 응답
     * @throws jakarta.validation.ConstraintViolationException 요청 데이터 유효성 검증 실패 시
     */
    @PostMapping("/api-keys")
    public ResponseEntity<CreateApiKeyResponseDto> createApiKey(
            @Valid @RequestBody CreateApiKeyRequestDto requestDto,
            @AuthenticationPrincipal @NotNull Merchant merchant
    ) {

        log.info("API 키 생성 요청: 가맹점 ID={}, 키 이름={}", merchant.getId(), requestDto.getName());

        // 방어적 코딩: expiresAt이 null인 경우 기본값 설정 (필드에 @NotBlank 있지만 안전장치로 유지)
        LocalDateTime expiresAt = requestDto.getExpiresAt() != null
                ? requestDto.getExpiresAt()
                : LocalDateTime.now().plusYears(1);

        CreateApiKeyResponseDto responseDto = apiKeyService.issueApiKey(
                requestDto.getName(),
                merchant,
                expiresAt);

        log.info("API 키 생성 완료: 가맹점 ID={}", merchant.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 가맹점용 API 키 목록 조회 엔드포인트
     * <p>
     * 현재 인증된 가맹점의 모든 API 키 목록을 페이징하여 조회합니다.
     * 활성 상태와 만료 여부를 포함한 API 키 정보를 반환합니다.
     * </p>
     *
     * @param merchant 현재 인증된 가맹점 정보
     * @param pageable 페이징 정보
     * @return 가맹점의 API 키 목록 페이지와 함께 200 OK 응답
     */
    @GetMapping("/api-keys")
    public ResponseEntity<Page<ApiKeyResponseDto>> getApiKeys(
            @AuthenticationPrincipal @NotNull Merchant merchant,
            Pageable pageable
    ) {

        log.info("API 키 목록 조회 요청: 가맹점 ID={}, 페이지={}, 크기={}",
                merchant.getId(), pageable.getPageNumber(), pageable.getPageSize());

        Page<ApiKeyResponseDto> responsePage = apiKeyService.getMerchantApiKeys(merchant, pageable);

        log.info("API 키 목록 조회 완료: 가맹점 ID={}, 총 키 개수={}, 페이지 개수={}",
                merchant.getId(), responsePage.getTotalElements(), responsePage.getTotalPages());
        return ResponseEntity.ok(responsePage);
    }

    /**
     * 가맹점용 API 키 비활성화 엔드포인트
     * <p>
     * 특정 API 키를 비활성화하는 엔드포인트입니다.
     * 가맹점은 자신이 소유한 API 키만 비활성화할 수 있습니다.
     * 비활성화된 API 키는 더 이상 인증에 사용할 수 없습니다.
     * </p>
     *
     * @param keyId    비활성화할 API 키의 ID
     * @param merchant 현재 인증된 가맹점 정보
     * @return 204 No Content 응답
     * @throws IllegalArgumentException API 키가 존재하지 않거나 가맹점이 소유하지 않은 경우
     * @throws IllegalStateException    API 키가 이미 비활성화된 경우
     */
    @PatchMapping("/api-keys/{keyId}/deactivate")
    public ResponseEntity<Void> deactivateApiKey(
            @PathVariable Long keyId,
            @AuthenticationPrincipal @NotNull Merchant merchant
    ) {

        log.info("API 키 비활성화 요청: 가맹점 ID={}, 키 ID={}", merchant.getId(), keyId);

        apiKeyService.deactivateApiKey(keyId, merchant);

        log.info("API 키 비활성화 완료: 가맹점 ID={}, 키 ID={}", merchant.getId(), keyId);
        return ResponseEntity.noContent().build();
    }

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