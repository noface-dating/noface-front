package com.duri.durifront.auth.filter;

import static com.duri.durifront.auth.domain.TokenType.ACCESS;

import com.duri.durifront.auth.domain.TokenType;
import com.duri.durifront.auth.exception.AuthErrorCode;
import com.duri.durifront.auth.exception.AuthException;
import com.duri.durifront.auth.provider.JwtTokenProvider;
import com.duri.durifront.auth.web.cookie.CookieService;
import com.duri.durifront.user.entity.UserRole;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CookieService cookieService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException
    {
        // 이미 인증된 경우 SKIP
        if (Objects.nonNull(SecurityContextHolder.getContext().getAuthentication())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. 쿠키에서 Access Token 추출
        Optional<String> optionalAccessToken = cookieService.getAccessToken(request);
        if (optionalAccessToken.isEmpty()) {
            // 인증이 필요없는 요청일 수 있으므로 그냥 통과
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = optionalAccessToken.get();

        try {
            // 2. JWT 검증 + Claims 파싱
            // 토큰 서명 + issuer + 만료시간 검증 --> Claims 파싱 수행
            Claims claims = jwtTokenProvider.parseClaims(accessToken);

            // 3. 토큰 타입 검증 (Access Token)
            TokenType tokenType = jwtTokenProvider.getTokenType(claims);
            if (tokenType != ACCESS) {
                throw new AuthException(AuthErrorCode.INVALID_TOKEN);
            }

            // 4. 사용자 정보 추출
            String userId = jwtTokenProvider.getUserId(claims);

            UserRole userRole = jwtTokenProvider.getRole(claims);
            List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + userRole.name())
            );

            // 5. Authentication 생성
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    authorities
            );

            // 6. SecurityContext 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (AuthException e) {
            // JwtExceptionFilter에서 처리하도록 전달
            throw e;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/auth/login");
    }
}
