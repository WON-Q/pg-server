package com.fisa.pg.repository;

import com.fisa.pg.entity.payment.Payment;
import com.fisa.pg.entity.transaction.Transaction;
import com.fisa.pg.entity.transaction.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    /**
     * 특정 결제(Payment)와 트랜잭션 상태로 트랜잭션을 조회하는 메서드
     *
     * @param payment 조회할 결제 객체
     * @param status  조회할 트랜잭션 상태
     * @return 조건에 맞는 트랜잭션 (없을 경우 빈 Optional)
     */
    Optional<Transaction> findByPaymentAndTransactionStatus(Payment payment, TransactionStatus status);
}
