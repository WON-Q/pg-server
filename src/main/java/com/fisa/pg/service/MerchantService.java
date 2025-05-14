package com.fisa.pg.service;

import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 가맹점 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepository;

    /**
     * 가맹점 ID로 활성화된 가맹점인지 확인합니다.
     *
     * @param merchantId 가맹점 ID
     * @return 유효한 가맹점 개체
     * @throws IllegalStateException 가맹점이 존재하지 않거나 비활성화된 경우
     */
    public Merchant validateActiveMerchant(Long merchantId) {
        return merchantRepository.findById(merchantId)
                .filter(Merchant::isActive)
                .orElseThrow(() -> new IllegalStateException("존재하지 않거나 비활성화된 가맹점입니다."));
    }
}