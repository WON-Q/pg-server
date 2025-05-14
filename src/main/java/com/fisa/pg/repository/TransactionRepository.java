package com.fisa.pg.repository;

import com.fisa.pg.entity.payment.Payment;
import com.fisa.pg.entity.transaction.Transaction;
import com.fisa.pg.entity.transaction.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTxnId(String txnId);

    /**
     * 특정 결제(Payment)와 트랜잭션 상태로 트랜잭션을 조회합니다.
     *
     * @param payment 조회할 결제 객체
     * @param status 조회할 트랜잭션 상태
     * @return 조건에 맞는 트랜잭션 (없을 경우 빈 Optional)
     */
    Optional<Transaction> findByPaymentAndTransactionStatus(Payment payment, TransactionStatus status);

    /**
     * 결제에 대한 환불 관련 트랜잭션 조회
     */
    @Query("SELECT t FROM Transaction t WHERE t.payment = :payment AND " +
            "(t.transactionStatus = com.fisa.pg.entity.transaction.TransactionStatus.REFUND_PENDING OR " +
            "t.transactionStatus = com.fisa.pg.entity.transaction.TransactionStatus.REFUND_APPROVED OR " +
            "t.transactionStatus = com.fisa.pg.entity.transaction.TransactionStatus.REFUND_FAILED)")
    List<Transaction> findByPaymentAndStatusIsRefund(@Param("payment") Payment payment);



}