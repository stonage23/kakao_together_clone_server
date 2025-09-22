package com.kakao.together.token.jwt;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 *
 * @param <T> 토큰 타입
 * @param <R>
 */
public abstract class AbstractRefreshTokenService<T, R> {

    protected final R repository;

    public AbstractRefreshTokenService(final R repository) {
        this.repository = repository;
    }

    /**
     * refresh token에서 얻은 유저 식별 claim으로 실제 DB에서 유저 데이터 조회 후 refresh token 발급에 사용할 유저 식별 claim 반환
     * @param refreshToken
     * @return
     */
    public abstract String getSubject(T refreshToken);

    public abstract List<GrantedAuthority> getAuthorities(String refreshToken);
}

