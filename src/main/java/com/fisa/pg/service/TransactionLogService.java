package com.fisa.pg.service;

import com.fisa.pg.dto.response.TransactionLogResponseDto;
import com.fisa.pg.entity.transaction.TransactionLog;
import com.fisa.pg.entity.transaction.TransactionStatus;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.repository.TransactionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionLogService {

    private final TransactionLogRepository transactionLogRepository;

    /**
     * 가맹점의 트랜잭션 로그 목록 조회 (가맹점용)
     * <p>
     * 가맹점 자신의 트랜잭션 로그를 조회합니다.
     * </p>
     *
     * @param merchant 인증된 가맹점
     * @param pageable 페이징 정보
     * @return 트랜잭션 로그 응답 DTO 페이지
     */
    @PostAuthorize("hasRole('MERCHANT')")
    @Transactional(readOnly = true)
    public Page<TransactionLogResponseDto> getMerchantTransactionLogs(Merchant merchant, Pageable pageable) {
        log.info("가맹점 트랜잭션 로그 조회: 가맹점ID={}", merchant.getId());
        Page<TransactionLog> logs = transactionLogRepository.findByMerchantOrderByCreatedAtDesc(merchant, pageable);
        return logs.map(TransactionLogResponseDto::from);
    }

    /**
     * 가맹점 트랜잭션 로그 필터링 조회
     * <p>
     * 다양한 조건으로 트랜잭션 로그를 필터링하여 조회합니다.
     * </p>
     *
     * @param merchant   가맹점 정보
     * @param startDate  조회 시작일시 (null 가능)
     * @param endDate    조회 종료일시 (null 가능)
     * @param statuses   트랜잭션 상태 목록 (null 가능)
     * @param methods    결제 수단 목록 (null 가능)
     * @param searchTerm 검색어 (null 가능)
     * @param pageable   페이징 정보
     * @return 필터링된 트랜잭션 로그 목록
     */
    @PostAuthorize("hasRole('MERCHANT')")
    @Transactional(readOnly = true)
    public Page<TransactionLogResponseDto> getMerchantFilteredTransactionLogs(
            Merchant merchant,
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<TransactionStatus> statuses,
            List<String> methods,
            String searchTerm,
            Pageable pageable) {

        log.info("가맹점 트랜잭션 로그 필터링 조회: 가맹점ID={}, 기간={} ~ {}, 상태={}, 결제수단={}, 검색어={}",
                merchant.getId(), startDate, endDate, statuses, methods, searchTerm);

        // 기본 조건: 가맹점 ID로 필터링
        Specification<TransactionLog> spec = Specification.where(
                (root, query, cb) -> cb.equal(root.get("merchant"), merchant));

        // 날짜 범위 필터
        if (startDate != null && endDate != null) {
            spec = spec.and((root, query, cb) ->
                    cb.between(root.get("createdAt"), startDate, endDate));
        }

        // 상태 필터
        if (statuses != null && !statuses.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("status").in(statuses));
        }

        // 결제 수단 필터
        if (methods != null && !methods.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("transaction").get("method").in(methods));
        }

        // 검색어 필터 (트랜잭션 ID나 고객명)
        if (searchTerm != null && !searchTerm.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(root.get("transaction").get("transactionId"), "%" + searchTerm + "%"),
                            cb.like(root.get("message"), "%" + searchTerm + "%")
                    ));
        }

        // 정렬 기본값: 생성일시 내림차순
        Page<TransactionLog> logs = transactionLogRepository.findAll(spec,
                pageable.getSort().isEmpty() ?
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                                Sort.by(Sort.Direction.DESC, "createdAt")) :
                        pageable);

        return logs.map(TransactionLogResponseDto::from);
    }


}