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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 가맹점용 트랜잭션 로그 조회 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/merchants/transactions/logs")
public class MerchantTransactionLogController {

    private final TransactionLogService transactionLogService;


    /**
     * 가맹점 기본 트랜잭션 로그 조회 API
     *
     * @param merchant 인증된 가맹점
     * @param pageable 페이징 정보
     * @return 트랜잭션 로그 목록 페이지
     */
    @GetMapping()
    public ResponseEntity<BaseResponse<Page<TransactionLogResponseDto>>> getTransactionLogs(
            @AuthenticationPrincipal Merchant merchant,
            Pageable pageable) {

        log.info("가맹점 기본 트랜잭션 로그 조회 요청: 가맹점ID={}", merchant.getId());

        Page<TransactionLogResponseDto> result = transactionLogService.getMerchantTransactionLogs(merchant, pageable);

        log.info("가맹점 기본 트랜잭션 로그 조회 완료: 가맹점ID={}, 총 {}건", merchant.getId(), result.getTotalElements());
        return ResponseEntity.ok(BaseResponse.onSuccess("가맹점 트랜잭션 로그 조회 성공", result));
    }

    /**
     * 가맹점 트랜잭션 로그 필터링 조회 API
     * <p>
     * 가맹점이 자신의 트랜잭션 로그를 조회합니다.
     * 다양한 필터링 옵션을 제공합니다.
     * </p>
     *
     * @param merchant   인증된 가맹점
     * @param startDate  조회 시작일시 (선택)
     * @param endDate    조회 종료일시 (선택)
     * @param statuses   트랜잭션 상태 목록 (선택)
     * @param methods    결제 수단 목록 (선택)
     * @param searchTerm 검색어 (트랜잭션ID 등) (선택)
     * @param pageable   페이징 정보
     * @return 트랜잭션 로그 목록 페이지
     */
    @GetMapping("/filter")
    public ResponseEntity<BaseResponse<Page<TransactionLogResponseDto>>> getFilteredTransactionLogs(
            @AuthenticationPrincipal Merchant merchant,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) List<TransactionStatus> statuses,
            @RequestParam(required = false) List<String> methods,
            @RequestParam(required = false) String searchTerm,
            Pageable pageable) {

        log.info("가맹점 트랜잭션 로그 조회 요청: 가맹점ID={}, 기간={} ~ {}, 상태={}, 결제수단={}, 검색어={}",
                merchant.getId(), startDate, endDate, statuses, methods, searchTerm);

        Page<TransactionLogResponseDto> result = transactionLogService.getMerchantFilteredTransactionLogs(
                merchant, startDate, endDate, statuses, methods, searchTerm, pageable);

        log.info("가맹점 트랜잭션 로그 조회 완료: 가맹점ID={}, 총 {}건", merchant.getId(), result.getTotalElements());
        return ResponseEntity.ok(BaseResponse.onSuccess("가맹점 트랜잭션 로그 조회 성공", result));
    }

}