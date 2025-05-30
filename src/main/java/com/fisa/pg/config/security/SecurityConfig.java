package com.fisa.pg.config.security;

import com.fisa.pg.config.security.filter.JwtAuthenticationFilter;
import com.fisa.pg.config.security.filter.PaymentTokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final PaymentTokenAuthenticationFilter paymentTokenAuthenticationFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 공통 보안 설정
    private HttpSecurity configureCommon(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }

    @Bean
    @Order(1) // 결제 관련 - PaymentTokenAuthenticationFilter 적용
    public SecurityFilterChain paymentTokenFilterChain(HttpSecurity http) throws Exception {
        return configureCommon(http)
                .securityMatcher("/prepare", "/payments/orders/**", "/api/payments/refund")
                .authorizeHttpRequests(request ->
                        request.anyRequest().hasAuthority("MERCHANT")
                )
                .addFilterBefore(paymentTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    @Order(2) // 관리자 대시보드 - JWT 인증 필요
    public SecurityFilterChain adminJwtFilterChain(HttpSecurity http) throws Exception {
        return configureCommon(http)
                .securityMatcher("/api/admin/**")
                .authorizeHttpRequests(request ->
                        request.anyRequest().hasAuthority("ADMIN")
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    @Order(3) // 가맹점 대시보드 - JWT 인증 필요
    public SecurityFilterChain merchantJwtFilterChain(HttpSecurity http) throws Exception {
        return configureCommon(http)
                .securityMatcher("/api/merchants/**", "/api/merchant/**")
                .authorizeHttpRequests(request ->
                        request.anyRequest().hasAuthority("MERCHANT")
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    @Order(4) // 인증이 필요 없는 결제 관련 엔드포인트
    public SecurityFilterChain paymentPublicFilterChain(HttpSecurity http) throws Exception {
        return configureCommon(http)
                .securityMatcher("/method", "/payments/authorize", "/payment/ui/**")
                .authorizeHttpRequests(request ->
                        request.anyRequest().permitAll()
                )
                .build();
    }

    @Bean
    @Order(5) // 인증이 필요 없는 인증 관련 엔드포인트
    public SecurityFilterChain authPublicFilterChain(HttpSecurity http) throws Exception {
        return configureCommon(http)
                .securityMatcher("/api/auth/login", "/api/auth/admin/login")
                .authorizeHttpRequests(request ->
                        request.anyRequest().permitAll()
                )
                .build();
    }


    @Bean
    @Order(6) // 기타 허용된 공개 엔드포인트
    public SecurityFilterChain miscPublicFilterChain(HttpSecurity http) throws Exception {
        return configureCommon(http)
                .securityMatcher("/actuator/health", "/favicon.ico", "/public/**", "/images/**", "/css/**", "/js/**")
                .authorizeHttpRequests(request ->
                        request.anyRequest().permitAll()
                )
                .build();
    }

    @Bean
    @Order(Integer.MAX_VALUE) // 그 외 모든 요청 거부
    public SecurityFilterChain denyAllFilterChain(HttpSecurity http) throws Exception {
        return configureCommon(http)
                .securityMatcher("/**")
                .authorizeHttpRequests(request ->
                        request.anyRequest().denyAll()
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}