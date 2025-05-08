package com.fisa.pg.entity.settlement;

import com.fisa.pg.entity.user.Merchant;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 가맹점별 일별 정산 정보
 */
@Table(name = "merchant_settlement")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MerchantSettlement {

    /**
     * 정산 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 가맹점 정보 (Merchant 1 : MerchantSettlement N)
     * 한 가맹점은 여러 개의 정산 내역을 가질 수 있음
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    /**
     * 정산 날짜 (ex: 2025-05-06)
     */
    @Column(name = "settlement_date", nullable = false)
    private LocalDate settlementDate;

    /**
     * 총 결제 금액
     */
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    /**
     * 부가세 금액
     */
    @Column(name = "vat_amount", nullable = false)
    private BigDecimal vatAmount;

    /**
     * 실 지급액 (총액 - 수수료 - 세금 등)
     */
    @Column(name = "net_amount", nullable = false)
    private BigDecimal netAmount;

    /**
     * 정산 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MerchantSettlementStatus status;

    /**
     * 정산 완료 시각
     */
    @Column(name = "settled_at", nullable = true)
    private LocalDateTime settledAt;

    /**
     * 정산 배치 Job ID (스케줄러 기준)
     */
    @Column(name = "settlement_batch_id", nullable = true)
    private String settlementBatchId;

}

