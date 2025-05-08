package com.fisa.pg.entity.refund;

import com.fisa.pg.entity.payment.Payment;
import com.fisa.pg.entity.transaction.Transaction;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 사용자 환불 요청을 관리하는 엔티티
 */
@Table(name = "refund")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Refund {

    /**
     * 환불 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 어떤 결제에 대한 환불인지
     * Payment 1 : Refund N 관계 (하나의 결제에 여러 번 부분 환불 가능)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    /**
     * 어떤 트랜잭션에 대한 환불인지
     * Transaction 1 : Refund N 관계 (하나의 트랜잭션에 여러 부분 환불 가능)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    /**
     * 환불 금액
     */
    @Column(name = "refund_amount", nullable = false)
    private Long refundAmount;

    /**
     * 환불 요청 시각
     */
    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    /**
     * 실제 환불 완료 시각
     */
    @Column(name = "refunded_at", nullable = true)
    private LocalDateTime refundedAt;

    /**
     * 환불 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "refund_status", nullable = false)
    private RefundStatus refundStatus;

    /**
     * 사용자가 입력한 사유 (선택)
     */
    @Column(columnDefinition = "TEXT", nullable = true)
    private String reason;

}
