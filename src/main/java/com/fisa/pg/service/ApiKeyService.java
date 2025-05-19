package com.fisa.pg.service;

import com.fisa.pg.dto.response.AdminApiKeyListResponseDto;
import com.fisa.pg.dto.response.ApiKeyCreatedResponseDto;
import com.fisa.pg.dto.response.MerchantApiKeyListResponseDto;
import com.fisa.pg.entity.auth.ApiKey;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.exception.ApiKeyAccessDeniedException;
import com.fisa.pg.exception.ApiKeyNotFoundException;
import com.fisa.pg.repository.ApiKeyRepository;
import com.fisa.pg.utils.ApiKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * API 키 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyGenerator apiKeyGenerator;

    /**
     * API 키 발급 메서드 (가맹점용)
     * @param keyName   API 키 이름
     * @param merchant  API 키를 발급받는 가맹점
     * @param expiresAt 만료일 (지정하지 않으면 기본값으로 1년 후 설정)
     * @return 생성된 API 키 정보
     */
    @Transactional
    public ApiKeyCreatedResponseDto issueApiKey(String keyName, Merchant merchant, LocalDateTime expiresAt) {
        String accessKey = apiKeyGenerator.generateAccessKey();
        String secretKey = apiKeyGenerator.generateSecretKey();

        ApiKey apiKey = ApiKey.createNewApiKey(merchant, keyName, accessKey, secretKey, expiresAt);

        apiKeyRepository.save(apiKey);

        log.info("API 키 발급 완료: 가맹점={}, 키이름={}", merchant.getName(), keyName);

        return ApiKeyCreatedResponseDto.from(apiKey);
    }

    /**
     * 가맹점 소유의 API 키를 비활성화하는 메서드 (가맹점용)
     * @param keyId    비활성화할 API 키의 ID
     * @param merchant 인증된 가맹점 객체
     */
    @Transactional
    public void deactivateApiKey(Long keyId, Merchant merchant) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .orElseThrow(ApiKeyNotFoundException::new);

        // 해당 가맹점의 API 키인지 확인
        if (!apiKey.getMerchant().getId().equals(merchant.getId())) {
            throw new ApiKeyAccessDeniedException();
        }

        apiKey.deactivate();
        apiKeyRepository.save(apiKey);

        log.info("API 키 비활성화 완료: 키ID={}, 가맹점={}", keyId, merchant.getName());
    }

    /**
     * 가맹점 API 키 목록 조회 메서드 (가맹점용)
     * @param merchant 조회 대상 가맹점
     * @return 가맹점 API 키 목록 DTO
     */
    @Transactional(readOnly = true)
    public MerchantApiKeyListResponseDto getMerchantApiKeys(Merchant merchant) {
        List<ApiKey> apiKeys = apiKeyRepository.findByMerchant(merchant);

        List<MerchantApiKeyListResponseDto.ApiKeyInfo> apiKeyInfos = apiKeys.stream()
                .map(MerchantApiKeyListResponseDto.ApiKeyInfo::from)
                .collect(Collectors.toList());

        return MerchantApiKeyListResponseDto.from(apiKeyInfos);
    }

    /**
     * 관리자용 전체 API 키 목록 페이징 조회 메서드
     *
     * @param pageable 페이징 정보
     * @return 페이지로 감싼 API 키 목록 DTO
     */
    @Transactional(readOnly = true)
    public Page<AdminApiKeyListResponseDto.ApiKeyInfo> getPagedApiKeys(Pageable pageable) {
        Page<ApiKey> apiKeysPage = apiKeyRepository.findAll(pageable);
        return apiKeysPage.map(AdminApiKeyListResponseDto.ApiKeyInfo::from);
    }

}