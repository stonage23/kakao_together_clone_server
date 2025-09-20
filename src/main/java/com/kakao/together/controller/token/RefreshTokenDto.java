package com.kakao.together.controller.token;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RefreshTokenDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class TokenRefreshRequest {
        @NotBlank(message = "refresh token을 필수로 포함해야 합니다.")
        private String refreshToken;
    }
}
