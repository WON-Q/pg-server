package com.fisa.pg.config.security;

import com.fisa.pg.config.security.filter.PaymentTokenAuthenticationFilter;
import com.fisa.pg.config.security.filter.JwtAuthenticationFilter;
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
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PaymentTokenAuthenticationFilter paymentTokenAuthenticationFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 공통 설정을 추출한 메서드
    private HttpSecurity configureCommon(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }

    @Bean
    @Order(1) // 앱카드 API 관련 필터 체인
    public SecurityFilterChain appCardFilterChain(HttpSecurity http) throws Exception {
        return configureCommon(http)
                .securityMatcher("/payments/authorize", "/payments/authorize/**", "/authorization", "/authorization/**")
                .authorizeHttpRequests(request -> request.anyRequest().permitAll())
                .build();
    }

    @Bean
    @Order(2) // 주문 생성 - Payment Token 관련 필터 체인
    public SecurityFilterChain paymentTokenFilterChain(HttpSecurity http) throws Exception {
        return configureCommon(http)
                .securityMatcher("/api/payment/**", "/api/order/**")
                .authorizeHttpRequests(request -> request.anyRequest().authenticated())
                .addFilterBefore(paymentTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    @Order(3) // JWT 토큰 - 대시보드 관련 필터 체인
    public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
        return configureCommon(http)
                .securityMatcher("/api/users/dashboard/**", "/api/admin/**", "/api/merchant/**")
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/merchant/**").hasRole("MERCHANT")
                        .anyRequest().hasAnyRole("ADMIN", "MERCHANT"))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    @Order(4) // Opaque 토큰 필터 체인
    public SecurityFilterChain opaqueTokenFilterChain(HttpSecurity http) throws Exception {
        return configureCommon(http)
                .securityMatcher("/api/external/**")
                .authorizeHttpRequests(request -> request.anyRequest().authenticated())
                // 여기에 Opaque 토큰 필터 추가 필요
                .build();
    }

    @Bean
    @Order(5) // 인증이 필요 없는 기본 요청용 필터 체인
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        return configureCommon(http)
                .securityMatcher("/api/auth/**", "/actuator/health", "/public/**")
                .authorizeHttpRequests(request -> request.anyRequest().permitAll())
                .build();
    }

    @Bean
    @Order(6) // 그 외 모든 요청에 대한 필터 체인
    public SecurityFilterChain fallbackFilterChain(HttpSecurity http) throws Exception {
        return configureCommon(http)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/merchant/**").hasRole("MERCHANT")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
