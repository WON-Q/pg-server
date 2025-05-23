package com.fisa.pg.service;

import com.fisa.pg.dto.response.TransactionLogResponseDto;
import com.fisa.pg.entity.transaction.TransactionStatus;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.repository.TransactionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionLogService {

    private final TransactionLogRepository transactionLogRepository;

    @Transactional(readOnly = true)
    public Page<TransactionLogResponseDto> getMerchantTransactionLogs(
            Merchant merchant,
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<TransactionStatus> statuses,
            List<String> methods,
            String searchTerm,
            Pageable pageable) {

        log.info("[Service] 트랜잭션 로그 필터 조회 | merchantId={} | 기간: {}~{} | 상태: {} | 수단: {} | 검색: {}",
                merchant.getId(), startDate, endDate, statuses, methods, searchTerm);

        return transactionLogRepository.findByFilters(merchant, startDate, endDate, statuses, methods, searchTerm, pageable);
    }
}
