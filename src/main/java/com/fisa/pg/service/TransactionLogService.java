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
     * 가맹점 트랜잭션 로그 조회 (통합 메서드)
     * <p>
     * 필터링 조건 유무에 따라 기본 조회 또는 필터링 조회를 수행합니다.
     * </p>
     *
     * @param merchant   가맹점 정보
     * @param startDate  조회 시작일시 (null 가능)
     * @param endDate    조회 종료일시 (null 가능)
     * @param statuses   트랜잭션 상태 목록 (null 가능)
     * @param methods    결제 수단 목록 (null 가능)
     * @param searchTerm 검색어 (null 가능)
     * @param pageable   페이징 정보
     * @return 트랜잭션 로그 응답 DTO 페이지
     */
    @PostAuthorize("hasRole('MERCHANT')")
    @Transactional(readOnly = true)
    public Page<TransactionLogResponseDto> getMerchantTransactionLogs(
            Merchant merchant,
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<TransactionStatus> statuses,
            List<String> methods,
            String searchTerm,
            Pageable pageable) {

        log.info("가맹점 트랜잭션 로그 조회: 가맹점ID={}, 필터링={}",
                merchant.getId(),
                hasFilters(startDate, endDate, statuses, methods, searchTerm));

        // 필터 파라미터가 전혀 없는 경우 기본 조회, 아니면 필터링 조회
        if (!hasFilters(startDate, endDate, statuses, methods, searchTerm)) {
            Page<TransactionLog> logs = transactionLogRepository.findByMerchantOrderByCreatedAtDesc(merchant, pageable);
            return logs.map(TransactionLogResponseDto::from);
        } else {
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

    /**
     * 필터링 조건이 있는지 확인
     */
    private boolean hasFilters(
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<TransactionStatus> statuses,
            List<String> methods,
            String searchTerm) {
        return startDate != null || endDate != null ||
                (statuses != null && !statuses.isEmpty()) ||
                (methods != null && !methods.isEmpty()) ||
                (searchTerm != null && !searchTerm.isBlank());
    }


}