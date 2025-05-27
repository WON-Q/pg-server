package com.fisa.pg.controller;

import com.fisa.pg.dto.request.RefundRequestFromOneQOrderDto;
import com.fisa.pg.dto.response.BaseResponse;
import com.fisa.pg.dto.response.RefundResponseToOneQOrderDto;
import com.fisa.pg.service.RefundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 환불 요청을 처리하는 컨트롤러 클래스입니다.
 * <p>
 * PG 서버에서 원큐오더 서버로 환불 요청을 처리합니다.
 * </p>
 */
@Slf4j
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
    public ResponseEntity<BaseResponse<RefundResponseToOneQOrderDto>> refund(
            @RequestBody RefundRequestFromOneQOrderDto request
    ) {
        try {
            RefundResponseToOneQOrderDto response = refundService.refund(request);
            log.info("환불 응답 반환 전: {}", response);
            return ResponseEntity.ok(BaseResponse.onSuccess("환불 요청이 성공적으로 처리되었습니다.", response));

        } catch (Exception e) {
            log.error("환불 처리 중 오류 발생: {}", e.getMessage());
            log.error(e.getCause().toString());
            e.printStackTrace();
        }

        return ResponseEntity.badRequest()
                .body(BaseResponse.onInternalServerError("환불 요청 처리 중 오류가 발생했습니다."));
    }
}