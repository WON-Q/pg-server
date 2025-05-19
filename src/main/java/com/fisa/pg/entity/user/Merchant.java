package com.fisa.pg.entity.user;

import com.fisa.pg.entity.auth.ApiKey;
import com.fisa.pg.entity.payment.Payment;
import com.fisa.pg.entity.settlement.MerchantSettlement;
import com.fisa.pg.entity.transaction.Transaction;
import com.fisa.pg.entity.transaction.TransactionLog;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PG사에 등록된 가맹점 정보를 관리하는 엔티티
 */
@Table(name = "merchant")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Merchant {

    /**
     * 가맹점 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 가맹점 이름
     */
    @Column(nullable = false)
    private String name;

    /**
     * 대표자명
     */
    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    /**
     * 사업자 등록번호
     */
    @Column(name = "business_number", nullable = false, unique = true)
    private String businessNumber;

    /**
     * 가맹점 이메일 (로그인 ID로 사용)
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * 가맹점 비밀번호 (암호화하여 저장)
     */
    @Column(nullable = false)
    private String password;

    /**
     * 가맹점 휴대폰 번호
     */
    @Column(name = "phone_number", nullable = true)
    private String phoneNumber;

    /**
     * 정산받을 계좌번호
     */
    @Column(name = "settlement_account_number", nullable = false)
    private String settlementAccountNumber;

    /**
     * 가맹점 계약 관계 여부 (true: 계약 중, false: 해지)
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    /**
     * Transaction 상태가 변경될 때마다 PG사가 가맹점에 전송하는 Webhook URL
     */
    @Column(name = "webhook_url", nullable = true)
    private String webhookUrl;

    /**
     * 웹훅 시크릿 키
     */
    @Column(name = "webhook_secret_key", nullable = true)
    private String webhookSecretKey;

    /**
     * 웹훅 활성화 여부
     * true: 활성화, false: 비활성화
     */
    @Column(name = "webhook_enabled", nullable = true)
    private boolean isWebhookEnabled;

    /**
     * 마지막 로그인 시각
     */
    @Column(name = "last_login_at", nullable = true)
    private LocalDateTime lastLoginAt;

    /**
     * 계정 생성 시각
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 계정 정보 수정 시각
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 가맹점에서 발생한 결제 목록 (1:N 관계)
     * 한 가맹점에서 여러 결제가 발생할 수 있음
     */
    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    /**
     * 가맹점에서 발생한 트랜잭션 목록 (1:N 관계)
     * 한 가맹점에서 여러 트랜잭션이 발생할 수 있음
     */
    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    /**
     * 가맹점에서 발생한 트랜잭션 로그 목록 (1:N 관계)
     * 한 가맹점에서 여러 트랜잭션 로그가 생성될 수 있음
     */
    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL)
    @Builder.Default
    private List<TransactionLog> transactionLogs = new ArrayList<>();

    /**
     * 가맹점 정산 정보 목록 (1:N 관계)
     * 한 가맹점에 여러 정산 내역이 존재할 수 있음
     */
    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL)
    @Builder.Default
    private List<MerchantSettlement> settlements = new ArrayList<>();

    /**
     * 가맹점 API 키 목록 (1:N 관계)
     * 한 가맹점은 여러 API 키를 가질 수 있음 (예: 운영용, 테스트용)
     */
    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ApiKey> apiKeys = new ArrayList<>();

    /**
     * 비활성화 처리
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 비밀번호 변경 처리
     *
     * @param newPassword 새 비밀번호(암호화된 상태)
     */
    public void changePassword(String newPassword) {
        this.password = newPassword;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 로그인 시각 업데이트
     */
    public void updateLoginTime() {
        this.lastLoginAt = LocalDateTime.now();
    }

    /**
     * Webhook URL 업데이트
     */
    public void updateWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Webhook 시크릿 키 업데이트
     */
    public void updateWebhookSecretKey(String webhookSecretKey) {
        this.webhookSecretKey = webhookSecretKey;
        this.updatedAt = LocalDateTime.now();
    }

}