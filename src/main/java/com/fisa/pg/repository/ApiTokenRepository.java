package com.fisa.pg.repository;

import com.fisa.pg.entity.auth.ApiToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiTokenRepository extends JpaRepository<ApiToken, Long> {

    /**
     * 주어진 토큰(tokenValue)과 활성화 상태(isActive)가 true인 API 토큰을 조회하는 메서드
     *
     * @param tokenValue 조회할 토큰
     * @return 활성화된 API 토큰 정보
     */
    Optional<ApiToken> findByTokenValueAndIsActiveTrue(String tokenValue);

}