package com.fisa.pg.controller;

import com.fisa.pg.dto.response.BaseResponse;
import com.fisa.pg.dto.response.WebhookResponseDto;
import com.fisa.pg.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자용 웹훅 관리 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/webhooks")
public class AdminWebhookController {

    private final WebhookService webhookService;

    /**
     * 관리자용 웹훅 목록 조회 API
     *
     * @param pageable 페이징 정보
     * @return 웹훅 응답 DTO 페이지
     */
    @GetMapping
    public ResponseEntity<BaseResponse<Page<WebhookResponseDto>>> getAllWebhooks(Pageable pageable) {
        log.info("모든 웹훅 조회 요청: 페이지={}, 크기={}", pageable.getPageNumber(), pageable.getPageSize());

        Page<WebhookResponseDto> data = webhookService.getWebhookList(pageable);

        log.info("모든 웹훅 조회 완료: 총 {}건", data.getTotalElements());
        return ResponseEntity.ok(BaseResponse.onSuccess("모든 웹훅 조회 성공", data));
    }

}