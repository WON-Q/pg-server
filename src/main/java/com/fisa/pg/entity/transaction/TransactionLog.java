package com.fisa.pg.entity.transaction;

import com.fisa.pg.entity.user.Merchant;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 트랜잭션 처리 과정에서 발생하는 다양한 이벤트와 상태를 기록하는 엔티티
 * <br />
 * 사용자가 결제 요청을 했을 때부터 카드사 승인, 가맹점 승인, 환불 요청 등 다양한 상태를 기록하여
 * 가맹점이 PG사와의 거래 내역을 추적할 수 있도록 한다.
 */
@Table(name = "transaction_log")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TransactionLog {

    /**
     * 트랜잭션 로그 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 연관된 트랜잭션 정보 (Transaction 1 : TransactionLog N)
     * 하나의 트랜잭션에 여러 로그가 생성됨
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    /**
     * 메시지 (예: 인증 성공, 카드사 승인 요청됨 등)
     */
    @Lob
    @Column(nullable = false)
    private String message;

    /**
     * 트랜잭션 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false)
    private TransactionStatus status;

    /**
     * 연관된 가맹점 정보 (Merchant 1 : TransactionLog N)
     * 로깅 시점의 가맹점 정보 추적용
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    /**
     * 로그 생성 시각
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}

