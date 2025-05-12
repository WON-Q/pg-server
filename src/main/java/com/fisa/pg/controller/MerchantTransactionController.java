package com.fisa.pg.controller;

import com.fisa.pg.dto.response.MerchantTransactionListResponseDto;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.service.MerchantTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 가맹점 사용자 전용 트랜잭션 조회 컨트롤러
 */
@RestController
@RequestMapping("/api/merchant/transactions")
@RequiredArgsConstructor
public class MerchantTransactionController {

    private final MerchantTransactionService merchantTransactionService;

    /**
     * 로그인한 가맹점 사용자의 트랜잭션 목록 조회
     */
    @GetMapping
    @PreAuthorize("hasRole('MERCHANT')")
    public List<MerchantTransactionListResponseDto> getMyTransactions(@RequestAttribute("merchant") Merchant merchant) {
        return merchantTransactionService.getTransactionsForMerchant(merchant);
    }
}
