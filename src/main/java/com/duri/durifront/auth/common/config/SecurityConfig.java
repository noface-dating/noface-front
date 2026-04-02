package com.duri.durifront.auth.common.config;

import com.duri.durifront.auth.filter.JwtAuthenticationFilter;
import com.duri.durifront.auth.filter.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 기본 설정
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // URL 권한 설정
                // TODO: 허용할 URL 추가
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스 허용
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/*.png").permitAll()
                        // 페이지 라우팅 허용
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/signup/**").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/onboarding/**").permitAll()
                        .requestMatchers("/setup/**").permitAll()
                        .requestMatchers("/messages/**").permitAll()
                        .requestMatchers("/profile/**").permitAll()
                        .requestMatchers("/community").permitAll()
                        .requestMatchers("/likes").permitAll()
                        .requestMatchers("/tier-missions").permitAll()
                        .requestMatchers("/support/**").permitAll()
                        // API 허용
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated()
                )

                // JWT 필터
                .addFilterBefore(jwtExceptionFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();

    }
}
