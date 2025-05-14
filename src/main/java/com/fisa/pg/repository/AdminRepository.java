package com.fisa.pg.repository;

import com.fisa.pg.entity.user.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 관리자 엔티티에 대한 데이터 액세스 인터페이스
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * 이메일로 관리자 조회
     *
     * @param email 관리자 이메일
     * @return 관리자 정보 Optional
     */
    Optional<Admin> findByEmail(String email);

}