package com.kakao.together.controller.dto;

import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpHeaders;

public class TokenContainer {

    private String accessHeader;
    private String refreshHeader;
    private String accessToken;
    private String refreshToken;

    private HttpHeaders httpHeaders;

    public TokenContainer(String accessHeader, String refreshHeader, String accessToken, String refreshToken) {
        this.accessHeader=accessHeader;
        this.refreshHeader=refreshHeader;
        this.accessToken=accessToken;
        this.refreshToken=refreshToken;
    }

    public TokenContainer(String accessHeader, String accessToken) {
        this.accessHeader=accessHeader;
        this.accessToken=accessToken;
    }

    @PostConstruct
    private void init() {
        HttpHeaders headers = new HttpHeaders();
        if (refreshToken != null) {
            headers.set(refreshHeader, refreshToken);
            headers.set(accessHeader, accessToken);
        } else {
            headers.set(accessHeader, accessToken);
        }
    }

    public HttpHeaders getHttpHeaders() {return this.httpHeaders;}
    public String getRefreshToken() {return this.refreshToken;}
}
