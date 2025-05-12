package com.fisa.pg.service;

import com.fisa.pg.dto.request.WebhookUrlRequestDto;
import com.fisa.pg.dto.response.WebhookUrlResponseDto;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 가맹점 관련 비즈니스 로직 처리 서비스
 */
@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepository;


    /**
     * MERCHANT Webhook URL 등록 또는 수정
     */
    @Transactional
    public void updateWebhookUrl(Merchant merchant, WebhookUrlRequestDto dto) {
        merchant.changeWebhookUrl(dto.getWebhookUrl());
        merchantRepository.save(merchant);

    }

    /**
     * MERCHANT Webhook URL 조회
     */
    @Transactional(readOnly = true)
    public WebhookUrlResponseDto getWebhookUrl(Merchant merchant) {
        return WebhookUrlResponseDto.builder()
                .webhookUrl(merchant.getWebhookUrl())
                .build();
    }

    /**
     * MERCHANT Webhook URL 삭제
     */
    @Transactional
    public void deleteWebhookUrl(Merchant merchant) {
        merchant.removeWebhookUrl();
        merchantRepository.save(merchant);
    }
}
