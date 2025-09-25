package com.kakao.together.token;

import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider implements TokenProvider {

    private final TokenService tokenService;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;
    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    private static final String BEARER = "Bearer ";
    private static final String AUTHORITIES_CLAIM = "auth";

    @Override
    public TokenContainer generateTokenContainer(Authentication authentication) {
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        List<String> authorities = principal.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).toList();
        Map<String, Object> claims = Map.of(
                AUTHORITIES_CLAIM, authorities
                , "userId", principal.getUserId()
                , "email", principal.getEmail()
        );
        String accessToken = createAccessToken(principal.getUsername(), claims);
        String refreshToken = createRefreshToken(principal.getUsername(), claims);
        return new TokenContainer(accessToken, refreshToken);
    }

    private String createAccessToken(String subject, Map<String, Object> claims) {
        return tokenService.buildToken(claims, subject, accessTokenExpirationPeriod);
    }

    private String createRefreshToken(String subject, Map<String, Object> claims) {
        return tokenService.buildToken(claims, subject, refreshTokenExpirationPeriod);
    }

    @Override
    public String removeBearerPrefix(@NotNull String token) {
        if (!token.startsWith(BEARER)) {
            log.warn("Bearer prefix가 없는 토큰");
            throw new CustomException(ErrorCode.NOT_MATCH_BEARER);
        }
        return token.replace(BEARER, "");
    }

    @Override
    public Authentication getAuthentication(String token) {

        Claims claims = tokenService.extractAllClaims(token);
        List<String> extractedAuthorities = claims.get(AUTHORITIES_CLAIM, List.class);
        Collection<? extends GrantedAuthority> authorities =
                extractedAuthorities.stream()
                        .map(autority -> new SimpleGrantedAuthority(autority))
                        .toList();

        CustomUserDetails principal = new CustomUserDetails(
                claims.getSubject(),
                "",
                claims.get("userId", Long.class),
                claims.get("email", String.class),
                authorities,
                true, true
        );

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }
}
