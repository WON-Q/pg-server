package com.fisa.pg.repository;

import com.fisa.pg.dto.response.TransactionLogResponseDto;
import com.fisa.pg.entity.transaction.TransactionLog;
import com.fisa.pg.entity.transaction.TransactionStatus;
import com.fisa.pg.entity.user.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {

    @Query("""
        SELECT new com.fisa.pg.dto.response.TransactionLogResponseDto(t)
        FROM TransactionLog t
        WHERE t.merchant = :merchant
        AND (:startDate IS NULL OR t.createdAt >= :startDate)
        AND (:endDate IS NULL OR t.createdAt <= :endDate)
        AND (:statuses IS NULL OR t.status IN :statuses)
        AND (:methods IS NULL OR t.transaction.method IN :methods)
        AND (
            :searchTerm IS NULL OR :searchTerm = '' OR 
            t.transaction.transactionId LIKE CONCAT('%', :searchTerm, '%') OR 
            t.message LIKE CONCAT('%', :searchTerm, '%')
        )
        ORDER BY t.createdAt DESC
        """)
    Page<TransactionLogResponseDto> findByFilters(
            @Param("merchant") Merchant merchant,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("statuses") List<TransactionStatus> statuses,
            @Param("methods") List<String> methods,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );
}
