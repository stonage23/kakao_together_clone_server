package com.kakao.together.token.jwt;

import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.token.TokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
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

    private static final String BEARER = "Bearer ";
    private static final String AUTHORITIES_CLAIM = "auth";

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
    public String buildToken(Map<String, Object> claims, String subject, Long expirationPeriod) {
        Date now = new Date();
        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .and()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(now.getTime() + expirationPeriod))
                .claims(claims)
                .signWith(secretKey)
                .compact();
    }

    private Jws<Claims> parseToken(String token) {
        try {
            return jwtParser
                    .parseSignedClaims(token);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN, "서명되지 않은 jwt토큰: token = {}"+ token);
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN, "jwt토큰 파싱 실패: token = " + token + " : " + e);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN, "jwt토큰이 null 또는 적절하지 못한 상태: token = " + token);
        }
    }

    @Override
    public Claims extractAllClaims(String token) {
        return parseToken(token).getPayload();
    }
}