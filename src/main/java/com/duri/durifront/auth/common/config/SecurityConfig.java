package com.duri.durifront.auth.common.config;

import com.duri.durifront.auth.filter.JwtAuthenticationFilter;
import com.duri.durifront.auth.filter.JwtExceptionFilter;
import com.duri.durifront.auth.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

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
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스 허용
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/*.png").permitAll()

                        // 에러 페이지 허용 (템플릿 에러 발생 시 /error로 포워딩되어 403 방지)
                        .requestMatchers("/error").permitAll()

                        // 페이지 라우팅 허용
                        .requestMatchers("/").permitAll()
						.requestMatchers("/main").permitAll()
                        .requestMatchers("/signup/**").permitAll()
                        .requestMatchers("/login", "/auth/login").permitAll()
                        .requestMatchers("/onboarding/**").permitAll()
						.requestMatchers("/face-preference/**").permitAll()
                        .requestMatchers("/setup/**").permitAll()
                        .requestMatchers("/community").permitAll()
                        .requestMatchers("/support/**").permitAll()
                        .requestMatchers("/api/**").permitAll()

                        // 로그인 필요
                        .requestMatchers("/messages/**").authenticated()
                        .requestMatchers("/profile/**").authenticated()
                        .requestMatchers("/mypage/**").authenticated()
                        .requestMatchers("/likes").authenticated()
                        .requestMatchers("/tier-missions").authenticated()

                        .anyRequest().authenticated()
                )

                // 인증 실패 시 처리
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
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

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
