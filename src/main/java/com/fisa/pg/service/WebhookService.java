package com.fisa.pg.service;

import com.fisa.pg.dto.request.CreateWebhookRequestDto;
import com.fisa.pg.dto.response.CreateWebhookResponseDto;
import com.fisa.pg.dto.response.WebhookResponseDto;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.exception.MerchantNotFoundException;
import com.fisa.pg.repository.MerchantRepository;
import com.fisa.pg.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WebhookService {

    private final MerchantRepository merchantRepository;

    /**
     * 가맹점 웹훅 생성 메서드
     *
     * @param merchantId 가맹점 ID
     * @param requestDto 웹훅 생성 요청 DTO
     * @return 웹훅 응답 DTO
     */
    @PostAuthorize("hasRole('MERCHANT')")
    @Transactional
    public CreateWebhookResponseDto createWebhook(Long merchantId, CreateWebhookRequestDto requestDto) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new MerchantNotFoundException(merchantId));

        String webhookSecretKey = StringUtil.generateRandomString(20);
        merchant.updateWebhookUrl(requestDto.getUrl());
        merchant.updateWebhookSecretKey(webhookSecretKey);
        merchant.updateWebhookRegisteredAt();

        return CreateWebhookResponseDto.from(merchant);
    }

    /**
     * 관리자 전용 웹훅 목록 조회 메서드
     *
     * @param pageable 페이지 정보
     * @return 웹훅 목록 응답 DTO 페이지
     */
    public Page<WebhookResponseDto> getWebhookList(Pageable pageable) {
        return merchantRepository.findByWebhookUrlIsNotNullAndIsWebhookEnabledTrue(pageable)
                .map(WebhookResponseDto::from);
    }

}
