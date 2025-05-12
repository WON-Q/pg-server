package com.fisa.pg.service;

import com.fisa.pg.dto.response.MerchantTransactionListResponseDto;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 가맹점 사용자 전용 트랜잭션 조회 서비스
 */
@Service
@RequiredArgsConstructor
public class MerchantTransactionService {

    private final TransactionRepository transactionRepository;

    /**
     * 본인(가맹점)의 트랜잭션 목록 조회
     */
    @Transactional(readOnly = true)
    public List<MerchantTransactionListResponseDto> getTransactionsForMerchant(Merchant merchant) {
        return transactionRepository.findByMerchant(merchant).stream()
                .map(MerchantTransactionListResponseDto::from)
                .toList();
    }
}
