package com.fisa.pg.repository;

import com.fisa.pg.entity.transaction.TransactionLog;
import com.fisa.pg.entity.user.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;

public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long>, JpaSpecificationExecutor<TransactionLog> {

    /**
     * 가맹점별 트랜잭션 로그 내림차순 조회
     */
    Page<TransactionLog> findByMerchantOrderByCreatedAtDesc(Merchant merchant, Pageable pageable);

    /**
     * 가맹점별 특정 기간 트랜잭션 로그 내림차순 조회
     */
    Page<TransactionLog> findByMerchantAndCreatedAtBetweenOrderByCreatedAtDesc(
            Merchant merchant, LocalDateTime start, LocalDateTime end, Pageable pageable);

    /**
     * 모든 트랜잭션 로그 내림차순 조회
     */
    Page<TransactionLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}