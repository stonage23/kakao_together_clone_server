package com.kakao.together.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class JwtService {

    private final SecretKey secretKey;
    private final JwtParser jwtParser;

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

    public SecretKey getSecretKey() {return this.secretKey;}

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
                .claims(claims != null ? claims : new HashMap<>())
                .signWith(secretKey)
                .compact();
    }

    private Jws<Claims> parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
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

    public Claims extractAllClaims(String token) {
        return parseToken(token).getPayload();
    }
}