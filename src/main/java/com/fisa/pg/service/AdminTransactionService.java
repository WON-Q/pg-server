package com.fisa.pg.service;

import com.fisa.pg.dto.response.AdminTransactionListResponseDto;
import com.fisa.pg.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 관리자 전용 트랜잭션 조회 서비스 클래스
 * <br>
 * 전체 가맹점의 거래 정보를 조회하는 기능
 */
@Service
@RequiredArgsConstructor
public class AdminTransactionService {

    private final TransactionRepository transactionRepository;

    /**
     * 전체 트랜잭션 목록을 조회하여 관리자용 응답 DTO로 변환
     *
     * @return 전체 트랜잭션 정보를 담은 AdminTransactionListResponseDto 리스트
     */
    @Transactional(readOnly = true)
    public List<AdminTransactionListResponseDto> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(AdminTransactionListResponseDto::from)
                .toList();
    }
}
