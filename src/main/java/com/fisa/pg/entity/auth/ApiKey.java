package com.fisa.pg.entity.auth;

import com.fisa.pg.entity.user.Merchant;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 가맹점에서 PG사에 요청을 보낼 때 사용하는 인증 키를 저장하는 엔티티
 * <br/>
 * API Key의 Public Key와 Secret Key를 통해 가맹점은 토큰을 발급받으며,
 * 해당 토큰을 통해 PG사와 통신을 한다.
 */
@Table(name = "api_key")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ApiKey {

    /**
     * API 키 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 키 소유 가맹점 (Merchant 1 : ApiKey N)
     * 한 가맹점은 여러 API 키를 소유할 수 있음
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    /**
     * 키 이름 (ex. "운영용", "테스트용")
     */
    @Column(nullable = false)
    private String name;

    /**
     * 액세스 키
     */
    @Column(name = "access_key", nullable = false, unique = true, length = 128)
    private String accessKey;

    /**
     * 시크릿 키
     */
    @Column(name = "secret_key", nullable = false, length = 256)
    private String secretKey;

    /**
     * 발급 시각
     */
    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    /**
     * 만료 시각 (nullable = 영구키)
     */
    @Column(name = "expires_at", nullable = true)
    private LocalDateTime expiresAt;

    /**
     * 활성 여부
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    /**
     * API 키 비활성화 메서드
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 새로운 API 키를 생성합니다.
     *
     * @param merchant  API 키를 발급받는 가맹점
     * @param name      API 키 이름
     * @param accessKey 액세스 키
     * @param secretKey 시크릿 키
     * @param expiresAt 만료일
     * @return 생성된 API 키 엔티티
     */
    public static ApiKey createNewApiKey(Merchant merchant, String name,
                                         String accessKey, String secretKey,
                                         LocalDateTime expiresAt) {
        return ApiKey.builder()
                .merchant(merchant)
                .name(name)
                .accessKey(accessKey)
                .secretKey(secretKey)
                .issuedAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .isActive(true)
                .build();
    }

}
