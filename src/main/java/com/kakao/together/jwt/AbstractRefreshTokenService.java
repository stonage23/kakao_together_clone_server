package com.kakao.together.jwt;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 *
 * @param <T> 토큰 타입
 * @param <R>
 */
public abstract class AbstractRefreshTokenService<T, R> {

    protected final R repository;

    protected AbstractRefreshTokenService(final R repository) {
        this.repository = repository;
    }

    /**
     * 전달 받은 refresh token이 서버 저장소에 실제로 존재하는지 확인.
     * @param refreshToken
     * @return
     */
    abstract T findRefreshToken(T refreshToken);

    /**
     * refresh token을 서버에 저장하는 로직. 저장 방식에 따라 의존성 주입 및 적절히 구현
     * @param refreshToken
     */
    protected abstract void saveRefreshToken(T refreshToken);

    /**
     * refresh token에서 얻은 유저 식별 claim으로 실제 DB에서 유저 데이터 조회 후 refresh token 발급에 사용할 유저 식별 claim 반환
     * @param username
     * @return
     */
    public abstract String getUsername(String username) throws UsernameNotFoundException;
}

