package com.fisa.pg.repository;

import com.fisa.pg.entity.refund.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 환불 요청을 처리하는 리포지토리 인터페이스입니다.
 * <p>
 * 환불 요청에 대한 CRUD 작업을 수행합니다.
 * </p>
 */
public interface RefundRepository extends JpaRepository<Refund, Long> {
}