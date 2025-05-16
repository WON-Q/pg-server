package com.fisa.pg.config.security;

import com.fisa.pg.config.security.filter.BasicAuthenticationFilter;
import com.fisa.pg.config.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final BasicAuthenticationFilter basicAuthenticationFilter;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Rest API 사용으로 CSRF 비활성화.
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/auth/**", "/actuator/health").permitAll() // 인증이 필요 없는 API
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // 관리자 전용 API
                        .requestMatchers("/api/**").hasRole("MERCHANT") // 가맹점 전용 API
                        .anyRequest().authenticated() // 기타 모든 요청은 인증 필요
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT 사용으로 세션 비활성화.
                )
                .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class) // BasicAuthenticationFilter 필터 전에 JWT 필터 추가
                .addFilterBefore(basicAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // UsernamePasswordAuthenticationFilter 필터 전에 Basic 필터 추가
                .build();

    }

}
