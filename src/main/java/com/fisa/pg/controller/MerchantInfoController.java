package com.fisa.pg.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 로그인한 가맹점의 정보를 반환하는 컨트롤러
 */
@RestController
@RequestMapping("/api/merchant")
public class MerchantInfoController {

    /**
     * 현재 로그인한 가맹점의 ID 조회
     */
    @GetMapping("/me")
    public ResponseEntity<Long> getCurrentMerchantId(Authentication authentication) {
        Long merchantId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(merchantId);
    }
}
