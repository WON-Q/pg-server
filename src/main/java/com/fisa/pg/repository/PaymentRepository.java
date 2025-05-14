package com.fisa.pg.repository;

import com.fisa.pg.entity.payment.Payment;
import com.fisa.pg.entity.payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    /**
     * 특정 주문(orderId)과 가맹점(merchantId)에 대한 결제 존재 여부를 확인 - 중복 결제 방지 로직에 사용
     *
     * @param orderId    가맹점 내부 주문 ID
     * @param merchantId 가맹점 ID
     * @return 해당 조건에 맞는 Payment가 존재하면 Optional로 반환
     */
    Optional<Payment> findByOrderIdAndMerchantId(String orderId, Long merchantId);

    @Modifying
    @Transactional
    @Query("UPDATE Payment p SET p.paymentStatus = :status WHERE p.id IN " +
            "(SELECT t.payment.id FROM Transaction t WHERE t.txnId = :txnId)")
    int updateStatusByTransactionId(@Param("txnId") String txnId,
                                    @Param("status") PaymentStatus status);
}