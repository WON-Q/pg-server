package com.fisa.pg.entity.auth;

import com.fisa.pg.entity.user.Merchant;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * API Key를 기반으로 발급되는 인증 토큰을 저장하는 엔티티
 * <br />
 * 가맹점이 API Key와 Secret Key를 통해 토큰을 발급받으며
 * 해당 토큰을 기반으로 PG 시스템의 API를 호출할 때 사용된다.
 */
@Table(name = "api_token")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ApiToken {

    /**
     * 토큰 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 토큰 소유 가맹점 정보
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    /**
     * 연관된 API Key 정보 (로그인 토큰의 경우 null)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_key_id", nullable = true)
    private ApiKey apiKey;

    /**
     * 토큰 값 (형식: PG_{32글자의 알파벳으로 구성된 임의의 문자열})
     */
    @Column(name = "token_value", nullable = false, unique = true, length = 64)
    private String tokenValue;

    /**
     * 토큰 타입 (Bearer 등)
     */
    @Column(name = "token_type", nullable = false)
    private String tokenType;

    /**
     * 발급 시각
     */
    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    /**
     * 만료 시각
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * 활성 여부
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    /**
     * 토큰 비활성화
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 토큰 만료 확인
     *
     * @return 만료 여부
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}