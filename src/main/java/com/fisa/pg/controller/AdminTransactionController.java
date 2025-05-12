package com.fisa.pg.controller;

import com.fisa.pg.dto.response.AdminTransactionListResponseDto;
import com.fisa.pg.service.AdminTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 관리자 전용 트랜잭션 조회 컨트롤러
 */
@RestController
@RequestMapping("/api/admin/transactions")
@RequiredArgsConstructor
public class AdminTransactionController {

    private final AdminTransactionService adminTransactionService;

    /**
     * 전체 트랜잭션 목록 조회
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminTransactionListResponseDto> getAllTransactions() {
        return adminTransactionService.getAllTransactions();
    }
}
