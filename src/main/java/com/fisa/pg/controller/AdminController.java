package com.fisa.pg.controller;

import com.fisa.pg.dto.response.WebhookUrlAdminResponseDto;
import com.fisa.pg.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 관리자용 가맹점 관리 컨트롤러
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/merchants")
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/webhook-urls")
    public List<WebhookUrlAdminResponseDto> getAllWebhookUrls() {
        return adminService.getAllWebhookUrls();
    }
}



