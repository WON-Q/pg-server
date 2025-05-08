package com.fisa.pg.entity.transaction;

import com.fisa.pg.entity.payment.Payment;
import com.fisa.pg.entity.payment.PaymentMethod;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.entity.refund.Refund;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 거래 정보를 관리하는 엔티티
 * <br />
 * 한 건의 결제에 대한 트랜잭션 정보를 저장하며,
 * 결제 승인, 취소, 환불이 발생할 때마다 각각의 거래 정보를 담은 트랜잭션이 생성된다.
 *
 * @see <a href="https://docs.tosspayments.com/reference#transaction-%EA%B0%9D%EC%B2%B4">토스 - Transaction 객체</a>
 */
@Entity
@Table(name = "transaction")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Transaction {

    /**
     * 트랜잭션 ID (외부와 연동 가능한 고유 ID로, 직접 생성하여 사용한다.)
     */
    @Id
    @Column(name = "transaction_id", nullable = false, length = 64)
    private String transactionId;

    /**
     * 연관된 결제 정보 (Payment 1 : Transaction N)
     * 하나의 Payment에 여러 Transaction이 연결될 수 있음
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    /**
     * 결제 금액
     */
    @Column(nullable = false)
    private Long amount;

    /**
     * 통화 단위 (예: KRW)
     */
    @Column(nullable = false)
    private String currency;

    /**
     * 결제 수단 (예: APP_CARD)
     */
    @Column(nullable = false)
    private PaymentMethod method;

    /**
     * 결제 요청한 가맹점 정보
     * Transaction은 Merchant와 N:1 관계
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    /**
     * 트랜잭션 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false)
    private TransactionStatus transactionStatus;

    /**
     * 결제 요청 시각 (초기 트랜잭션 생성 시 저장)
     */
    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    /**
     * 앱카드 인증 완료 시각
     */
    @Column(name = "authenticated_at", nullable = true)
    private LocalDateTime authenticatedAt;

    /**
     * 결제 승인 시각 (승인된 경우만 저장)
     */
    @Column(name = "approved_at", nullable = true)
    private LocalDateTime approvedAt;

    /**
     * 결제 취소 시각 (취소된 경우만 저장)
     */
    @Column(name = "canceled_at", nullable = true)
    private LocalDateTime canceledAt;
    
    /**
     * 트랜잭션 로그 목록
     * 하나의 트랜잭션에 여러 로그가 생성됨 (1:N 관계)
     */
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TransactionLog> transactionLogs = new ArrayList<>();
    
    /**
     * 트랜잭션에 대한 환불 정보
     * 하나의 트랜잭션에 여러 환불이 발생할 수 있음 (부분 환불 포함) (1:N 관계)
     */
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Refund> refunds = new ArrayList<>();

}
