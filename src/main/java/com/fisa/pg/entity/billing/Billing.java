package com.fisa.pg.entity.billing;

import com.fisa.pg.entity.payment.Payment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Billing 엔티티
 * 결제에 대한 청구 정보를 관리
 */
@Entity
@Table(name = "billing")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Billing {

    /**
     * 청구 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 연관된 결제 정보 (Payment 1 : Billing 1)
     * 결제와 청구는 1:1 관계
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_billing_payment"))
    private Payment payment;

    /**
     * 청구 금액
     */
    @Column(nullable = false)
    private Long amount;

    /**
     * 청구 시각
     */
    @Column(name = "billed_at", nullable = false)
    private LocalDateTime billedAt;

    /**
     * 청구 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "billing_status", nullable = false)
    private BillingStatus billingStatus;

    /**
     * 결제 수단 유형 (신용카드, 체크카드 등)
     */
    @Column(name = "billing_type", nullable = false)
    private String billingType;

    /**
     * 은행 코드 (은행 결제 시)
     */
    @Column(name = "bank_code", nullable = true)
    private String bankCode;

    /**
     * 은행 트랜잭션 ID (은행 결제 시)
     */
    @Column(name = "bank_transaction_id", nullable = true)
    private String bankTransactionId;

    /**
     * 카드사 코드 (카드 결제 시)
     */
    @Column(name = "card_company_code", nullable = true)
    private String cardCompanyCode;

    /**
     * 카드사 트랜잭션 ID (카드 결제 시)
     */
    @Column(name = "card_transaction_id", nullable = true)
    private String cardTransactionId;

}
