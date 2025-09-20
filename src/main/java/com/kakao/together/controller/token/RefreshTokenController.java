package com.kakao.together.controller.token;

import com.kakao.together.service.token.RefreshTokenRepository;
import com.kakao.together.token.jwt.AbstractRefreshTokenService;
import com.kakao.together.token.jwt.JwtService;
import com.kakao.together.token.jwt.exception.TokenNotFoundInStoreException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.kakao.together.controller.token.RefreshTokenDto.TokenRefreshRequest;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class RefreshTokenController {

    private final JwtService jwtService;
    private final AbstractRefreshTokenService refreshTokenService;
    private final RefreshTokenRepository<String> refreshTokenRepository;

    @PostMapping("/refresh")
    public ResponseEntity<TokenContainer> refreshToken(@RequestBody @Valid TokenRefreshRequest request) {
        String value = refreshTokenRepository.findRefreshToken(request.getRefreshToken());
        if (value.isEmpty()) {
            throw new TokenNotFoundInStoreException("해당 refresh토큰을 저장소에서 찾을 수 없음. 재로그인 필요");
        }
        String subject = refreshTokenService.getSubject(request.getRefreshToken());
        List<GrantedAuthority> authorities = refreshTokenService.getAuthorities(request.getRefreshToken());
        TokenContainer tokenContainer = jwtService.generateTokenContainer(subject, Map.of("auth", authorities));
        refreshTokenRepository.saveRefreshToken(tokenContainer.getRefreshToken(), subject);
        refreshTokenRepository.deleteRefreshToken(request.getRefreshToken());
        return ResponseEntity.ok(tokenContainer);
    }
}
