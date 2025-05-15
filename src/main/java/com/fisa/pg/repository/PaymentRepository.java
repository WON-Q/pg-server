package com.fisa.pg.repository;

import com.fisa.pg.entity.payment.Payment;
import com.fisa.pg.entity.user.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * 주문 ID와 가맹점 정보로 결제 정보를 조회하는 메서드
     *
     * @param orderId  주문 ID
     * @param merchant 가맹점 정보
     * @return 결제 정보
     */
    Optional<Payment> findByOrderIdAndMerchant(String orderId, Merchant merchant);
}
