package com.fisa.pg.repository;

import com.fisa.pg.entity.user.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * 이메일(관리자 ID)로 관리자를 조회하는 메서드
     *
     * @param email 관리자 이메일
     * @return 관리자 정보
     */
    Optional<Admin> findByEmail(String email);
}