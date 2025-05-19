package com.fisa.pg.controller;

import com.fisa.pg.dto.request.ApiKeyCreateRequestDto;
import com.fisa.pg.dto.response.ApiKeyCreatedResponseDto;
import com.fisa.pg.dto.response.MerchantApiKeyListResponseDto;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 가맹점용 API 키 관리 컨트롤러
 * <p>
 * 이 컨트롤러는 가맹점에게 자신의 API 키를 생성, 조회 및 비활성화할 수 있는 엔드포인트를 제공합니다.
 * 가맹점은 인증된 상태에서만 이 API를 사용할 수 있으며, 자신의 API 키만 관리할 수 있습니다.
 * API 키는 가맹점이 PG 시스템과 연동하기 위해 필요한 인증 수단입니다.
 * </p>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/merchants/api-keys")
public class MerchantApiKeyController {

    private final ApiKeyService apiKeyService;

    /**
     * 가맹점용 API 키 발급 엔드포인트
     * <p>
     * 가맹점이 새로운 API 키를 생성할 수 있는 엔드포인트입니다.
     * 이름과 만료일을 지정하여 API 키를 생성할 수 있으며,
     * 만료일이 지정되지 않은 경우 기본값으로 1년 후로 설정됩니다.
     * </p>
     *
     * @param requestDto API 키 생성 요청 정보 (이름, 만료일)
     * @param merchant 현재 인증된 가맹점 정보
     * @return 생성된 API 키 정보와 함께 201 Created 응답
     * @throws jakarta.validation.ConstraintViolationException 요청 데이터 유효성 검증 실패 시
     */
    @PostMapping
    public ResponseEntity<ApiKeyCreatedResponseDto> createApiKey(
            @Valid @RequestBody ApiKeyCreateRequestDto requestDto,
            @AuthenticationPrincipal Merchant merchant) {

        log.info("API 키 생성 요청: 가맹점 ID={}, 키 이름={}", merchant.getId(), requestDto.getName());

        // 방어적 코딩: expiresAt이 null인 경우 기본값 설정 (필드에 @NotBlank 있지만 안전장치로 유지)
        LocalDateTime expiresAt = requestDto.getExpiresAt() != null
                ? requestDto.getExpiresAt()
                : LocalDateTime.now().plusYears(1);

        ApiKeyCreatedResponseDto responseDto = apiKeyService.issueApiKey(
                requestDto.getName(),
                merchant,
                expiresAt);

        log.info("API 키 생성 완료: 가맹점 ID={}", merchant.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 가맹점용 API 키 목록 조회 엔드포인트
     * <p>
     * 현재 인증된 가맹점의 모든 API 키 목록을 조회합니다.
     * 활성 상태와 만료 여부를 포함한 API 키 정보를 반환합니다.
     * </p>
     *
     * @param merchant 현재 인증된 가맹점 정보
     * @return 가맹점의 API 키 목록과 함께 200 OK 응답
     */
    @GetMapping
    public ResponseEntity<MerchantApiKeyListResponseDto> getApiKeys(
            @AuthenticationPrincipal Merchant merchant) {

        log.info("API 키 목록 조회 요청: 가맹점 ID={}", merchant.getId());

        MerchantApiKeyListResponseDto responseDto = apiKeyService.getMerchantApiKeys(merchant);

        log.info("API 키 목록 조회 완료: 가맹점 ID={}, 키 개수={}",
                merchant.getId(), responseDto.getApiKeys().size());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 가맹점용 API 키 비활성화 엔드포인트
     * <p>
     * 특정 API 키를 비활성화하는 엔드포인트입니다.
     * 가맹점은 자신이 소유한 API 키만 비활성화할 수 있습니다.
     * 비활성화된 API 키는 더 이상 인증에 사용할 수 없습니다.
     * </p>
     *
     * @param keyId 비활성화할 API 키의 ID
     * @param merchant 현재 인증된 가맹점 정보
     * @return 204 No Content 응답
     * @throws IllegalArgumentException API 키가 존재하지 않거나 가맹점이 소유하지 않은 경우
     * @throws IllegalStateException API 키가 이미 비활성화된 경우
     */
    @PatchMapping("/{keyId}/deactivate")
    public ResponseEntity<Void> deactivateApiKey(
            @PathVariable Long keyId,
            @AuthenticationPrincipal Merchant merchant) {

        log.info("API 키 비활성화 요청: 가맹점 ID={}, 키 ID={}", merchant.getId(), keyId);

        apiKeyService.deactivateApiKey(keyId, merchant);

        log.info("API 키 비활성화 완료: 가맹점 ID={}, 키 ID={}", merchant.getId(), keyId);
        return ResponseEntity.noContent().build();
    }
}