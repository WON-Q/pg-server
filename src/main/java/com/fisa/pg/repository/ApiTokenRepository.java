package com.fisa.pg.repository;

import com.fisa.pg.entity.auth.ApiToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiTokenRepository extends JpaRepository<ApiToken, Long> {

}