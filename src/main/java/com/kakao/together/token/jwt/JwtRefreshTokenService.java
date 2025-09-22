package com.kakao.together.token.jwt;

import com.kakao.together.controller.member.dto.MemberCommand;
import com.kakao.together.domain.repository.MemberRepository;
import com.kakao.together.token.jwt.exception.TokenUserNotFoundException;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtRefreshTokenService extends AbstractRefreshTokenService<String, MemberRepository> {

    private final JwtService jwtService;

    private static final String PARAM_USERNAME = "username";

    protected JwtRefreshTokenService(MemberRepository repository, JwtService jwtService) {
        super(repository);
        this.jwtService = jwtService;
    }

    @Override
    public String getSubject(String refreshToken) {
        Claims claims = jwtService.extractAllClaims(refreshToken);
        String username = claims.getSubject();
        return String.valueOf(
                repository.findById(Long.valueOf(username)).map(MemberCommand::fromEntity)
                .orElseThrow(() -> new TokenUserNotFoundException("refreshtoken에서 얻은 유저 정보로 DB 유저 조회 실패; username: " + username))
                .getId());
    }

    @Override
    public List<GrantedAuthority> getAuthorities(String refreshToken) {
        Claims claims = jwtService.extractAllClaims(refreshToken);
        return claims.get("auth", List.class);
    }
}
