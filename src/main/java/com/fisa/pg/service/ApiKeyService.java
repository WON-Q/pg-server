package com.fisa.pg.service;

import com.fisa.pg.dto.response.ApiKeyResponseDto;
import com.fisa.pg.dto.response.CreateApiKeyResponseDto;
import com.fisa.pg.entity.auth.ApiKey;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.exception.ApiKeyAccessDeniedException;
import com.fisa.pg.exception.ApiKeyNotFoundException;
import com.fisa.pg.repository.ApiKeyRepository;
import com.fisa.pg.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * API 키 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Slf4j
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

    /**
     * API 키 발급 메서드 (가맹점용)
     *
     * @param keyName   API 키 이름
     * @param merchant  API 키를 발급받는 가맹점
     * @param expiresAt 만료일 (지정하지 않으면 기본값으로 1년 후 설정)
     * @return 생성된 API 키 정보
     */
    @PostAuthorize("hasRole('MERCHANT')")
    @Transactional
    public CreateApiKeyResponseDto issueApiKey(String keyName, Merchant merchant, LocalDateTime expiresAt) {
        String accessKey = StringUtil.generateRandomString(20);
        String secretKey = StringUtil.generateRandomString(40);

        ApiKey apiKey = ApiKey.builder()
                .merchant(merchant)
                .name(merchant.getName())
                .accessKey(accessKey)
                .secretKey(secretKey)
                .issuedAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .isActive(true)
                .build();

        apiKeyRepository.save(apiKey);

        log.info("API 키 발급 완료: 가맹점={}, 키이름={}", merchant.getName(), keyName);

        return CreateApiKeyResponseDto.from(apiKey);
    }

    /**
     * 가맹점 소유의 API 키를 비활성화하는 메서드 (가맹점용)
     *
     * @param keyId    비활성화할 API 키의 ID
     * @param merchant 인증된 가맹점 객체
     */
    @PostAuthorize("hasRole('MERCHANT')")
    @Transactional
    public void deactivateApiKey(Long keyId, Merchant merchant) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .orElseThrow(ApiKeyNotFoundException::new);

        // 해당 가맹점의 API 키인지 확인
        if (!apiKey.getMerchant().getId().equals(merchant.getId())) {
            throw new ApiKeyAccessDeniedException();
        }

        apiKey.deactivate();

        log.info("API 키 비활성화 완료: 키ID={}, 가맹점={}", keyId, merchant.getName());
    }

    /**
     * 가맹점 API 키 목록 조회 메서드 (가맹점용)
     *
     * @param merchant 조회 대상 가맹점
     * @param pageable 페이징 정보
     * @return 가맹점 API 키 목록 페이지
     */
    @PostAuthorize("hasRole('MERCHANT')")
    @Transactional(readOnly = true)
    public Page<ApiKeyResponseDto> getMerchantApiKeys(Merchant merchant, Pageable pageable) {
        Page<ApiKey> apiKeysPage = apiKeyRepository.findByMerchant(merchant, pageable);
        return apiKeysPage.map(ApiKeyResponseDto::from);
    }

}