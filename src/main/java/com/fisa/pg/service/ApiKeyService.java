package com.fisa.pg.service;

import com.fisa.pg.dto.response.ApiKeyResponseDto;
import com.fisa.pg.entity.auth.ApiKey;
import com.fisa.pg.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * API 키 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    /**
     * 관리자용 전체 API 키 목록 페이징 조회 메서드
     *
     * @param pageable 페이징 정보
     * @return 페이지로 감싼 API 키 목록 DTO
     */
    @PostAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<ApiKeyResponseDto> getApiKeyList(Pageable pageable) {
        Page<ApiKey> apiKeysPage = apiKeyRepository.findAll(pageable);
        return apiKeysPage.map(ApiKeyResponseDto::from);
    }

}