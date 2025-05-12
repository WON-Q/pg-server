package com.fisa.pg.service;

import com.fisa.pg.dto.response.AdminApiKeyInfoDto;
import com.fisa.pg.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    public List<AdminApiKeyInfoDto> getAllApiKeys() {
        return apiKeyRepository.findAll()
                .stream()
                .map(AdminApiKeyInfoDto::from)
                .toList();
    }
}
