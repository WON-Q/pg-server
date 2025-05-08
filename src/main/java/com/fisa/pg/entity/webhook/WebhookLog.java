package com.fisa.pg.entity.webhook;

import com.fisa.pg.entity.transaction.Transaction;
import com.fisa.pg.entity.transaction.TransactionStatus;
import com.fisa.pg.entity.user.Merchant;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Webhook 전송 로그를 관리하는 엔티티
 * <br />
 * Transaction 상태가 변경될 때마다 Webhook을 전송하고, 그 결과를 기록한다.
 * <br />
 * Webhook 전송 시도 횟수와 마지막 시도 시각을 기록하여 재시도 로직을 구현할 수 있다.
 */
@Table(name = "webhook_log")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WebhookLog {

    /**
     * 웹훅 로그 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 연관된 가맹점 정보
     * 가맹점과 웹훅 로그는 1:N 관계
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    /**
     * 웹훅 URL
     * 가맹점별 webhook_url 필드에서 가져옴
     */
    @Column(name = "webhook_url", nullable = false)
    private String webhookUrl;

    /**
     * 연결된 트랜잭션 정보 
     * 트랜잭션과 웹훅 로그는 1:N 관계
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    /**
     * Transaction 상태
     * 로깅 시점의 트랜잭션 상태 기록
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false)
    private TransactionStatus transactionStatus;

    /**
     * 웹훅 엔드포인트로 전송된 JSON 데이터(페이로드)
     */
    @Lob
    @Column(nullable = false)
    private String payload;

    /**
     * 웹훅 전송 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WebhookStatus status;

    /**
     * 웹훅이 전송의 시도 시각
     */
    @Column(name = "attempt_at", nullable = false)
    private LocalDateTime attemptAt;

    /**
     * 웹훅 전송 실패 시 실패 이유
     * <br />
     * 성공한 경우 null로 설정된다.
     */
    @Column(name = "failure_reason", nullable = true)
    private String failureReason;

    /**
     * 재시도 카운트
     * 0부터 시작하여 재시도할 때마다 증가
     */
    @Column(name = "retry_count", nullable = false)
    private int retryCount;

}
