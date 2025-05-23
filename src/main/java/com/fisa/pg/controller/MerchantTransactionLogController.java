package com.fisa.pg.controller;

import com.fisa.pg.dto.response.BaseResponse;
import com.fisa.pg.dto.response.TransactionLogResponseDto;
import com.fisa.pg.entity.transaction.TransactionStatus;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.service.TransactionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/merchants/transactions/logs")
public class MerchantTransactionLogController {

    private final TransactionLogService transactionLogService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<TransactionLogResponseDto>>> getTransactionLogs(
            @AuthenticationPrincipal Merchant merchant,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) List<TransactionStatus> statuses,
            @RequestParam(required = false) List<String> methods,
            @RequestParam(required = false) String searchTerm,
            Pageable pageable) {

        log.info("[Controller] 트랜잭션 로그 조회 요청 | merchantId={} | page: {}", merchant.getId(), pageable);

        Page<TransactionLogResponseDto> result = transactionLogService.getMerchantTransactionLogs(
                merchant, startDate, endDate, statuses, methods, searchTerm, pageable
        );

        return ResponseEntity.ok(BaseResponse.onSuccess("가맹점 트랜잭션 로그 조회 성공", result));
    }
}
