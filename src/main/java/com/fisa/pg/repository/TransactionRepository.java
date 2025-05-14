package com.fisa.pg.repository;

import com.fisa.pg.entity.payment.Payment;
import com.fisa.pg.entity.transaction.Transaction;
import com.fisa.pg.entity.transaction.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTxnId(String txnId);
    Optional<Transaction> findByOrderId(String orderId);

    /**
     * 특정 결제(Payment)와 트랜잭션 상태로 트랜잭션을 조회합니다.
     *
     * @param payment 조회할 결제 객체
     * @param status 조회할 트랜잭션 상태
     * @return 조건에 맞는 트랜잭션 (없을 경우 빈 Optional)
     */
    Optional<Transaction> findByPaymentAndTransactionStatus(Payment payment, TransactionStatus status);

}