package com.fisa.pg.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 시스템 관리자 정보를 관리하는 엔티티
 * <br/>
 * 토큰을 발급하여 결제를 하는 기능은 없으며, 가맹점 정보를 조회하는 역할만 수행합니다.
 */
@Entity
@Table(name = "admin")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Admin {

    /**
     * 관리자 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 관리자 계정 ID (로그인 아이디로 사용)
     */
    @Column(name = "admin_id", nullable = false, unique = true)
    private String adminId;

    /**
     * 관리자 비밀번호 (암호화하여 저장)
     */
    @Column(nullable = false)
    private String password;

    /**
     * 관리자 이름
     */
    @Column(nullable = false)
    private String name;

    /**
     * 관리자 이메일
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * 관리자 휴대폰 번호
     */
    @Column(name = "phone_number", nullable = true)
    private String phoneNumber;

    /**
     * 계정 활성화 상태
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive;

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
     * 계정 활성화/비활성화 처리
     *
     * @param active 활성화 상태
     */
    public void setActive(boolean active) {
        this.isActive = active;
        this.updatedAt = LocalDateTime.now();
    }
}