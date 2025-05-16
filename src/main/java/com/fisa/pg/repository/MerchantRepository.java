package com.fisa.pg.repository;

import com.fisa.pg.entity.user.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    /**
     * 이메일(가맹점 ID)로 가맹점을 조회하는 메서드
     *
     * @param email 가맹점 이메일
     * @return 가맹점 정보
     */
    Optional<Merchant> findByEmail(String email);
}