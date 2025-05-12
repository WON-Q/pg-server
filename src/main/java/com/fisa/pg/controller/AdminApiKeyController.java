package com.fisa.pg.controller;

import com.fisa.pg.dto.response.AdminApiKeyInfoDto;
import com.fisa.pg.service.AdminApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/api-keys")
@RequiredArgsConstructor
public class AdminApiKeyController {

    private final AdminApiKeyService adminApiKeyService;

    @GetMapping
    public List<AdminApiKeyInfoDto> getApiKeyList() {
        return adminApiKeyService.getAllApiKeys();
    }
}