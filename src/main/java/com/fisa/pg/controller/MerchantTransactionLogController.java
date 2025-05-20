package com.fisa.pg.controller;

import com.fisa.pg.dto.response.BaseResponse;
import com.fisa.pg.dto.response.TransactionLogPage;
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

    /**
     * 가맹점 트랜잭션 로그 조회 API
     *
     * @param merchant   로그인한 가맹점
     * @param startDate  조회 시작일시 (선택)
     * @param endDate    조회 종료일시 (선택)
     * @param statuses   트랜잭션 상태 목록 (선택)
     * @param methods    결제 수단 목록 (선택)
     * @param searchTerm 검색어 (트랜잭션ID 등) (선택)
     * @param pageable   페이징 정보
     * @return 트랜잭션 로그 응답 DTO 페이지
     */
    @GetMapping
    public ResponseEntity<BaseResponse<TransactionLogPage<TransactionLogResponseDto>>> getTransactionLogs(
            @AuthenticationPrincipal Merchant merchant,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) List<TransactionStatus> statuses,
            @RequestParam(required = false) List<String> methods,
            @RequestParam(required = false) String searchTerm,
            Pageable pageable) {

        log.info("[요청] 트랜잭션 로그 조회 | merchantId={} | 기간: {} ~ {} | 상태: {} | 수단: {} | 검색어: {} | page: {}",
                merchant.getId(), startDate, endDate, statuses, methods, searchTerm, pageable);

        Page<TransactionLogResponseDto> result = transactionLogService.getMerchantTransactionLogs(
                merchant, startDate, endDate, statuses, methods, searchTerm, pageable
        );

        log.info("[응답] 트랜잭션 로그 조회 완료 | merchantId={} | 조회 건수: {}", merchant.getId(), result.getTotalElements());

        TransactionLogPage<TransactionLogResponseDto> pageResult = new TransactionLogPage<>(result);
        return ResponseEntity.ok(BaseResponse.onSuccess("가맹점 트랜잭션 로그 조회 성공", pageResult));
    }
}
