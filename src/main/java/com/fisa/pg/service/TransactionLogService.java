package com.fisa.pg.service;

import com.fisa.pg.dto.response.TransactionLogResponseDto;
import com.fisa.pg.entity.transaction.TransactionLog;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.repository.TransactionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionLogService {

    private final TransactionLogRepository transactionLogRepository;

    /**
     * 가맹점의 트랜잭션 로그 목록 조회 (가맹점용)
     * <p>
     * 가맹점 자신의 트랜잭션 로그를 조회합니다.
     * </p>
     *
     * @param merchant 인증된 가맹점
     * @param pageable 페이징 정보
     * @return 트랜잭션 로그 응답 DTO 페이지
     */
    @PostAuthorize("hasRole('MERCHANT')")
    @Transactional(readOnly = true)
    public Page<TransactionLogResponseDto> getMerchantTransactionLogs(Merchant merchant, Pageable pageable) {
        log.info("가맹점 트랜잭션 로그 조회: 가맹점ID={}", merchant.getId());
        Page<TransactionLog> logs = transactionLogRepository.findByMerchantOrderByCreatedAtDesc(merchant, pageable);
        return logs.map(TransactionLogResponseDto::from);
    }

}