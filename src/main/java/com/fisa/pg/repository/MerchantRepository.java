package com.fisa.pg.repository;

import com.fisa.pg.entity.user.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 가맹점(Merchant) 관련 DB 접근을 처리하는 리포지토리
 */
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    /**
     * 이메일로 가맹점 조회 (로그인용 등)
     */
    Optional<Merchant> findByEmail(String email);

    /**
     * 사업자 등록번호로 가맹점 조회 (중복 검증 등)
     */
    Optional<Merchant> findByBusinessNumber(String businessNumber);

    /**
     * 가맹점 이름으로 조회 (관리자용 등)
     */
    Optional<Merchant> findByName(String name);
}
