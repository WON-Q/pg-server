package com.fisa.pg.service;

import com.fisa.pg.dto.response.WebhookUrlAdminResponseDto;
import com.fisa.pg.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 관리자 전용 가맹점 관리 서비스
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    /**
     * 전체 가맹점의 Webhook URL 목록 조회 (관리자 전용)
     */
    @Transactional(readOnly = true)
    public List<WebhookUrlAdminResponseDto> getAllWebhookUrls() {
        return adminRepository.findAll().stream()
                .map(merchant -> WebhookUrlAdminResponseDto.builder()
                        .merchantId(merchant.getId())
                        .merchantName(merchant.getName())
                        .webhookUrl(merchant.getWebhookUrl())
                        .build()
                ).toList();
    }
}
