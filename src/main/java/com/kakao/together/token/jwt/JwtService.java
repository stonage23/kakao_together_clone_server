package com.kakao.together.token.jwt;

import com.kakao.together.controller.dto.TokenContainer;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.token.TokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class JwtService implements TokenService {

    private final SecretKey secretKey;
    private final JwtParser jwtParser;


    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;
    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;


    private static final String BEARER = "Bearer ";
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";

    /**
     * Secret key는 보안 및 일관성을 위해 외부에서 주입받아 Key 객체로 변환 후 사용
     *
     * @param secretKey
     */
    public JwtService(@Value("${jwt.secret}") String secretKey) {
        byte[] decoded = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(decoded);
        this.jwtParser = Jwts.parser().verifyWith(this.secretKey).build();
    }

    @Override
    public SecretKey getSecretKey() {return this.secretKey;}

    private String buildToken(Map<String, Object> claims, String subject, Long expirationPeriod) {
        Date now = new Date();
        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .and()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(now.getTime() + expirationPeriod))
                .claims(claims != null ? claims : new HashMap<>())
                .signWith(secretKey)
                .compact();
    }

    private String createAccessToken(Map<String, Object> claims) {
        return buildToken(claims, ACCESS_TOKEN_SUBJECT, accessTokenExpirationPeriod);
    }

    private String createRefreshToken(Map<String, Object> claims) {
        return buildToken(claims, REFRESH_TOKEN_SUBJECT, refreshTokenExpirationPeriod);
    }

    @Override
    public String createBearerAccessToken(Map<String, Object> claims) { return withBearerPrefix(createAccessToken(claims)); }

    @Override
    public String createBearerRefreshToken(Map<String, Object> claims) { return withBearerPrefix(createRefreshToken(claims)); }

    @Override
    public TokenContainer generateTokenContainerWithCommonClaims(Map<String, Object> claims) {
        String accessToken = createAccessToken(claims);
        String refreshToken = createRefreshToken(claims);
        return new TokenContainer(accessHeader, refreshHeader, accessToken, refreshToken);
    }

    private Jws<Claims> parseToken(String token) {
        try {
            return jwtParser
                    .parseSignedClaims(token);
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("서명되지 않은 jwt토큰: token = {}"+ token, e);
        } catch (JwtException e) {
            throw new JwtException("jwt토큰 파싱 실패: token = {}"+ token, e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("jwt토큰이 null 또는 적절하지 못한 상태: token = {}"+ token, e);
        } catch (RuntimeException e) {
            throw new RuntimeException("토큰 유효성 확인 도중 알 수 없는 에러 발생", e);
        }
    }

    @Override
    public String withBearerPrefix(String token) {
        return BEARER + token;
    }

    @Override
    public String removeBearerPrefix(@NotNull String token) {
        if (!token.startsWith(BEARER)) {
            log.error("Bearer prefix가 없는 토큰");
            throw new CustomException(ErrorCode.NOT_MATCH_BEARER);
        }
        return token.replace(BEARER, "");
    }

    @Override
    public Claims extractAllClaims(String token) {
        return parseToken(token).getPayload();
    }
}