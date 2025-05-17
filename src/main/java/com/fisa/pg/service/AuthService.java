package com.fisa.pg.service;

import com.fisa.pg.dto.request.LoginRequestDto;
import com.fisa.pg.dto.response.LoginResponseDto;
import com.fisa.pg.dto.response.TokenDto;
import com.fisa.pg.entity.user.Admin;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.entity.user.Role;
import com.fisa.pg.exception.InvalidCredentialsException;
import com.fisa.pg.repository.AdminRepository;
import com.fisa.pg.repository.MerchantRepository;
import com.fisa.pg.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MerchantRepository merchantRepository;

    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    /**
     * 가맹점 로그인 처리 메서드
     *
     * @param request 로그인 요청 DTO
     * @return 로그인 응답 DTO
     */
    @Transactional
    public LoginResponseDto handleMerchantLogin(LoginRequestDto request) {
        Merchant merchant = merchantRepository.findByEmail(request.getId())
                .orElseThrow(InvalidCredentialsException::new);

        if (!merchant.isActive()) {
            throw new InvalidCredentialsException();
        }

        if (!passwordEncoder.matches(request.getPassword(), merchant.getPassword())) {
            throw new InvalidCredentialsException();
        }

        merchant.updateLoginTime();

        TokenDto tokens = jwtUtil.generateTokens(merchant.getId(), Role.MERCHANT);

        return LoginResponseDto.builder()
                .id(merchant.getId())
                .name(merchant.getName())
                .email(merchant.getEmail())
                .token(tokens)
                .build();
    }

    /**
     * 관리자 로그인 처리 메서드
     *
     * @param request 로그인 요청 DTO
     * @return 로그인 응답 DTO
     */
    @Transactional
    public LoginResponseDto handleAdminLogin(LoginRequestDto request) {
        Admin admin = adminRepository.findByEmail(request.getId())
                .orElseThrow(InvalidCredentialsException::new);

        if (!admin.isActive()) {
            throw new InvalidCredentialsException();
        }

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new InvalidCredentialsException();
        }

        admin.updateLoginTime();

        TokenDto tokens = jwtUtil.generateTokens(admin.getId(), Role.ADMIN);

        return LoginResponseDto.builder()
                .id(admin.getId())
                .name(admin.getName())
                .email(admin.getEmail())
                .token(tokens)
                .build();
    }
}