package com.fisa.pg.entity.payment;

import com.fisa.pg.entity.transaction.Transaction;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.entity.billing.Billing;
import com.fisa.pg.entity.refund.Refund;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 클라이언트가 결제를 시도한 순간부터 결제가 완료될 때까지의 결제 메타 정보를 관리하는 엔티티
 */
@Entity
@Table(name = "payment", uniqueConstraints = {
        @UniqueConstraint(name = "uniq_order_merchant", columnNames = {"order_id", "merchant_id"})
})
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Payment {

    /**
     * 결제 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 가맹점 내 주문 고유 ID
     */
    @Column(name = "order_id", nullable = false)
    private String orderId;

    /**
     * 가맹점 연관 관계
     * 가맹점과 결제는 1:N 관계 (한 가맹점이 여러 결제를 처리)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    /**
     * 결제 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    /**
     * 결제 금액
     */
    @Column(nullable = false)
    private Long amount;

    /**
     * 결제 통화
     */
    @Column(nullable = false)
    private String currency;

    /**
     * 콜백 URL
     * 결제 완료 후 사용자를 리다이렉트할 URL
     */
    @Column(name = "callback_url", nullable = true)
    private String callbackUrl;

    /**
     * 결제 수단
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    /**
     * 거래 정보 (1:N 관계)
     * 하나의 결제에 여러 트랜잭션이 생성될 수 있음
     * (결제 승인, 취소, 환불 등)
     */
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    /**
     * 결제에 대한 청구 정보 (1:1 관계)
     * 하나의 결제에는 하나의 청구만 존재
     */
    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Billing billing;

    /**
     * 결제에 대한 환불 정보 (1:N 관계)
     * 하나의 결제에 여러 환불이 생성될 수 있음(부분 환불 포함)
     */
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Refund> refunds = new ArrayList<>();

    /**
     * 결제 상태를 업데이트하는 메서드
     *
     * @param newStatus 새로운 결제 상태
     */
    public void updatePaymentStatus(PaymentStatus newStatus) {
        this.paymentStatus = newStatus;
    }

}