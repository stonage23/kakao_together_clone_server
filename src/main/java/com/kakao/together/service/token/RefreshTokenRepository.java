package com.kakao.together.service.token;

public interface RefreshTokenRepository {

    /**
     * 전달 받은 refresh token이 서버 저장소에 실제로 존재하는지 확인.
     * @param refreshToken
     * @return
     */
    String findRefreshToken(String refreshToken);

    /**
     * refresh token을 서버에 저장하는 로직. 저장 방식에 따라 의존성 주입 및 적절히 구현
     * @param refreshToken
     */
    void saveRefreshToken(String refreshToken, String username);

    void deleteRefreshToken(String refreshToken);
}
