package com.fisa.pg.repository;

import com.fisa.pg.entity.auth.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

}
