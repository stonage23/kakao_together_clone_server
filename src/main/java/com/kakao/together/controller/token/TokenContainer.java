package com.kakao.together.controller.token;

public class TokenContainer {

    private TokenContainer() {}

    private String accessToken;
    private String refreshToken;

    public TokenContainer(String accessToken, String refreshToken) {
        this.accessToken=accessToken;
        this.refreshToken=refreshToken;
    }

    public String getAccessToken() { return this.accessToken; }
    public String getRefreshToken() { return this.refreshToken; }
}
