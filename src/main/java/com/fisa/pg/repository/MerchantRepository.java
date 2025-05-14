package com.fisa.pg.repository;

import com.fisa.pg.entity.user.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 가맹점 엔티티에 대한 데이터 액세스 인터페이스
 */
@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    /**
     * 이메일로 가맹점 조회
     *
     * @param email 가맹점 이메일
     * @return 가맹점 정보 Optional
     */
    Optional<Merchant> findByEmail(String email);

}