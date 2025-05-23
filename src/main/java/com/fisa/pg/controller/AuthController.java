package com.fisa.pg.controller;

import com.fisa.pg.dto.request.LoginRequestDto;
import com.fisa.pg.dto.response.BaseResponse;
import com.fisa.pg.dto.response.LoginResponseDto;
import com.fisa.pg.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponseDto>> loginMerchant(@RequestBody LoginRequestDto request) {
        System.out.println("📥 [컨트롤러 진입] 로그인 요청 받음: " + request.getEmail());
        LoginResponseDto data = authService.handleMerchantLogin(request);
        return ResponseEntity.ok(BaseResponse.onSuccess("로그인에 성공하였습니다.", data));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<BaseResponse<LoginResponseDto>> loginAdmin(@RequestBody LoginRequestDto request) {
        System.out.println("📥 [컨트롤러 진입] 로그인 요청 받음: " + request.getEmail());
        LoginResponseDto data = authService.handleAdminLogin(request);
        return ResponseEntity.ok(BaseResponse.onSuccess("로그인에 성공하였습니다.", data));
    }
}