package com.kakao.together.controller.token.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RefreshTokenDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class TokenRefreshRequest {
        private String refreshToken;
    }
}
