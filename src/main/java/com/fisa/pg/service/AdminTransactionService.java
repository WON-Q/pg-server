package com.fisa.pg.service;

import com.fisa.pg.dto.response.TransactionLogResponseDto;
import com.fisa.pg.repository.TransactionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTransactionService {

    private final TransactionLogRepository transactionLogRepository;

    @Transactional(readOnly = true)
    public Page<TransactionLogResponseDto> getAllTransactionLogs(Pageable pageable) {
        log.info("[서비스 호출] 전체 트랜잭션 로그 조회 시작");

        return transactionLogRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(TransactionLogResponseDto::from);
    }
}
