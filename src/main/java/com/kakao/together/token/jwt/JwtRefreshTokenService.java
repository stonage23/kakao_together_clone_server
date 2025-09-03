package com.kakao.together.token.jwt;

import com.kakao.together.controller.member.dto.MemberCommand;
import com.kakao.together.domain.repository.MemberRepository;
import com.kakao.together.token.jwt.exception.JwtAuthExceptions;
import org.springframework.stereotype.Component;

@Component
public class JwtRefreshTokenService extends AbstractRefreshTokenService<String, MemberRepository> {

    protected JwtRefreshTokenService(MemberRepository repository) {
        super(repository);
    }

    // TODO: Redis 기능 구현 이후 작성
    @Override
    public String findRefreshToken(String refreshToken) {
        return "";
    }

    // TODO: REDIS 기능 구현 이후 작성
    @Override
    public void saveRefreshToken(String refreshToken) {
    }

    @Override
    public String getUsername(String username) {
        return repository.findByEmail(username).map(MemberCommand::fromEntity)
                .orElseThrow(() -> new JwtAuthExceptions.RefreshTokenUserNotFoundException("refreshtoken에서 얻은 유저 정보로 DB 유저 조회 실패; username: " + username))
                .getEmail();
    }
}
