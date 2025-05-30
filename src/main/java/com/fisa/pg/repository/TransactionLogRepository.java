package com.fisa.pg.repository;

import com.fisa.pg.entity.transaction.TransactionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionLogRepository extends JpaRepository<TransactionLog, String> {

    Page<TransactionLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
