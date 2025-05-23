package com.fisa.pg.controller;

import com.fisa.pg.dto.request.RefundRequestFromOneQOrderDto;
import com.fisa.pg.dto.response.RefundResponseToOneQOrderDto;
import com.fisa.pg.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 환불 요청을 처리하는 컨트롤러 클래스입니다.
 * <p>
 * PG 서버에서 원큐오더 서버로 환불 요청을 처리합니다.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class RefundController {

    private final RefundService refundService;

    /**
     * 환불 요청을 처리하는 API
     *
     * @param request 환불 요청 정보
     * @return 환불 응답 정보
     */
    @PostMapping("/refund")
    public ResponseEntity<RefundResponseToOneQOrderDto> refund(
            @RequestBody RefundRequestFromOneQOrderDto request
    ) {
        RefundResponseToOneQOrderDto response = refundService.refund(request);
        return ResponseEntity.ok(response);
    }
}