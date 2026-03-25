package com.duri.durifront.auth.provider;

import com.duri.durifront.auth.common.properties.JwtProperties;
import com.duri.durifront.auth.domain.TokenType;
import com.duri.durifront.auth.exception.AuthErrorCode;
import com.duri.durifront.auth.exception.AuthException;
import com.duri.durifront.user.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private static final String ROLE = "role";
    private static final String TOKEN_TYPE = "token_type";

    private final JwtKeyProvider jwtKeyProvider;
    private final JwtProperties jwtProperties;

    /**
     * JWT 토큰을 파싱하여 Claims 추출
     *
     * <p>
     *     - 토큰의 서명(Signature)을 Public Key로 검증
     *     - 발급자(issuer) 검증
     *     - 토큰 구조 및 유효성 검증
     * </p>
     *
     * <p>
     *     - 만료된 토큰의 경우, EXPIRED_TOKEN 예외 발생
     *     - 그 외 유효하지 않은 토큰은 INVALID_TOKEN 예외 발생
     * </p>
     *
     * @param token JWT 토큰 문자열
     * @return 파싱된 Claims 객체
     * @throws AuthException 토큰이 만료된 경우 또는 유효하지 않은 경우
     */
    public Claims parseClaims(String token) {
        if (Objects.isNull(token) || token.isBlank()) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }

        try {
            return Jwts.parser()
                    .verifyWith(jwtKeyProvider.getPublicKey())
                    .requireIssuer(jwtProperties.getIssuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new AuthException(AuthErrorCode.EXPIRED_TOKEN, e);
        } catch (JwtException e) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN, e);
        }
    }

    /**
     * Claims에서 사용자 식별자(userId)를 추출
     *
     * <p>
     *     - JWT 토큰의 sub(subject) 값을 반환
     *     - 토큰 파싱 과정에서 유효성 검증이 완료되었음을 전제로 함
     * </p>
     *
     * @param claims 파싱된 JWT Claims
     * @return 사용자 식별자 (userId)
     */
    public String getUserId(Claims claims) {
        // claims NULL 체크X (토큰 파싱 과정에서 예외 처리O)
        return claims.getSubject();
    }

    /**
     * Claims에서 사용자 권한(role)을 추출
     *
     * <p>
     *      - "role" Claim 값을 기반으로 UserRole 반환
     *      - 토큰 파싱 과정에서 유효성 검증이 완료되었음을 전제로 함
     *      - role 값이 없거나 Enum 변환에 실패할 경우 예외 발생
     * </p>
     *
     * @param claims 파싱된 JWT Claims
     * @return 사용자 권한 (userRole)
     * @throws AuthException role 값이 없거나 유효하지 않은 경우
     */
    public UserRole getRole(Claims claims) {
        try {
            // claims NULL 체크X (토큰 파싱 과정에서 예외 처리O)
            String role = claims.get(ROLE, String.class);

            if (Objects.isNull(role)) {
                throw new AuthException(AuthErrorCode.INVALID_TOKEN);
            }

            return UserRole.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN, e);
        }
    }

    /**
     * Claims에서 토큰 타입(TokenType)을 추출
     *
     * <p>
     *     - "token_type" Claim 값을 기반으로 TokenType 반환
     *     - Access Token / Refresh Token 구분에 사용
     *     - 토큰 파싱 과정에서 유효성 검증이 완료되었음을 전제로 함
     *     - token_type 값이 없거나 Enum 변환에 실패할 경우 예외 발생
     * </p>
     *
     * @param claims 파싱된 JWT Claims
     * @return 토큰 타입 (tokenType)
     * @throws AuthException token_type 값이 없거나 유효하지 않은 경우
     */
    public TokenType getTokenType(Claims claims) {
        try {
            // claims NULL 체크X (토큰 파싱 과정에서 예외 처리O)
            String tokenType = claims.get(TOKEN_TYPE, String.class);

            if (Objects.isNull(tokenType)) {
                throw new AuthException(AuthErrorCode.INVALID_TOKEN);
            }

            return TokenType.valueOf(tokenType);
        } catch (IllegalArgumentException e) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN, e);
        }
    }

    /**
     * Claims에서 JWT ID (jti)를 추출
     *
     * <p>
     *     - 토큰의 고유 식별자 (UUID) 반환
     *     - Refresh Token 블랙리스트 관리 및 추적에 사용할 예정
     *     - 토큰 파싱 과정에서 유효성 검증이 완료되었음을 전제로 함
     * </p>
     *
     * @param claims 파싱된 JWT Claims
     * @return JWT ID (jti)
     * @throws AuthException jti 값이 존재하지 않는 경우
     */
    public String getJti(Claims claims) {
        // claims NULL 체크X (토큰 파싱 과정에서 예외 처리O)
        String jti = claims.getId();

        if (Objects.isNull(jti)) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }

        return jti;
    }
}
