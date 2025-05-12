package com.fisa.pg.service;

import com.fisa.pg.dto.request.LoginRequestDto;
import com.fisa.pg.dto.response.LoginResponseDto;
import com.fisa.pg.dto.response.JwtTokenInfo;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.repository.MerchantRepository;
import com.fisa.pg.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public LoginResponseDto login(LoginRequestDto request) {
        Merchant merchant = merchantRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), merchant.getPassword())) {
            throw new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        merchant.updateLoginTime();
        merchantRepository.save(merchant);

        JwtTokenInfo tokenInfo = jwtProvider.generateToken(merchant.getId(), merchant.getEmail(), merchant.getRole());
        return LoginResponseDto.from(merchant, tokenInfo);
    }
}