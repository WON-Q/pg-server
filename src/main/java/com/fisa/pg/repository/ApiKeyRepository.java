package com.fisa.pg.repository;

import com.fisa.pg.entity.auth.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    /**
     * Access Key 기반으로 API 키 조회하는 메서드
     *
     * @param accessKey 조회할 액세스 키
     * @return 액세스 키와 일치하는 API 키
     */
    Optional<ApiKey> findByAccessKey(String accessKey);
}
