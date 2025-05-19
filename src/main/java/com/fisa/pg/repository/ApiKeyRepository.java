package com.fisa.pg.repository;

import com.fisa.pg.entity.auth.ApiKey;
import com.fisa.pg.entity.user.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * API 키 관련 데이터베이스 작업을 처리하는 레포지토리 인터페이스
 * JpaRepository를 상속받아 CRUD 작업을 지원
 */
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    /**
     * Access Key 기반으로 API 키 조회하는 메서드
     *
     * @param accessKey 조회할 액세스 키
     * @return 액세스 키와 일치하는 API 키
     */
    Optional<ApiKey> findByAccessKey(String accessKey);

    /**
     * 특정 가맹점이 소유한 모든 API 키 조회
     *
     * @param merchant 가맹점 정보
     * @return 가맹점의 API 키 목록
     */
    List<ApiKey> findByMerchant(Merchant merchant);
}