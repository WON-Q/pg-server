package com.fisa.pg.controller;

import com.fisa.pg.dto.response.AdminApiKeyListResponseDto;
import com.fisa.pg.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자용 API 키 관리 컨트롤러
 * <p>
 * 이 컨트롤러는 관리자에게 모든 가맹점의 API 키를 조회할 수 있는 엔드포인트를 제공합니다.
 * 관리자 인증이 필요한 API이며, 가맹점 별 API 키 발급 및 관리 현황을 모니터링하는 용도로 사용됩니다.
 * </p>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/api-keys")
public class AdminApiKeyController {

    private final ApiKeyService apiKeyService;

    /**
     * 전체 API 키 목록 페이징 조회 엔드포인트
     *
     * @param pageable 페이지 번호, 페이지 크기, 정렬 조건 등의 페이징 정보
     * @return 페이징된 API 키 목록과 함께 200 OK 응답
     * @throws IllegalArgumentException 페이징 파라미터가 유효하지 않은 경우 발생
     */
    @GetMapping
    public ResponseEntity<Page<AdminApiKeyListResponseDto.ApiKeyInfo>> getApiKeys(
            @PageableDefault(size = 20) Pageable pageable) {

        Page<AdminApiKeyListResponseDto.ApiKeyInfo> apiKeyInfoPage =
                apiKeyService.getPagedApiKeys(pageable);

        return ResponseEntity.ok(apiKeyInfoPage);
    }
}