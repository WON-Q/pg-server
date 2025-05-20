package com.fisa.pg.controller;

import com.fisa.pg.dto.response.BaseResponse;
import com.fisa.pg.dto.response.TransactionLogResponseDto;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.service.TransactionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}