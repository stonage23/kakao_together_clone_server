package com.kakao.together.token.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.together.token.jwt.exception.JwtAuthExceptions;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AbstractRefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;

    private static String[] noCheckUri = {"/auth/login", "/favicon.ico"};
    private static final String PARAM_USERNAME = "username";

    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;


    /**
     * Authentication 객체가 SecurityContext에 담기지 않았으면(AccessToken이 유효하지 않은 경우) 401에러 발생
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        boolean isExcludedUri = Arrays.stream(noCheckUri).anyMatch(request.getRequestURI()::contains);

        if (isExcludedUri) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Optional<String> refreshToken = extractRefreshToken(request);

            if (refreshToken.isPresent()) {
                try {
                    reIssueRefreshAndAccessToken(response, refreshToken.get());
                } catch (JwtAuthExceptions.RefreshTokenUserNotFoundException e) {
                    handleUnauthorized(response, e.getMessage());
                }
                return;
            }

            saveAuthentication(checkAccessTokenAndAuthentication(request));
            filterChain.doFilter(request, response);

        } catch (JwtAuthExceptions.RefreshTokenUserNotFoundException | JwtAuthExceptions.BedCredentialsException e) {
            log.warn("##### JwtAuthenticationExceptionHandler: {}; {}", e.getClass().getSimpleName(), e.getMessage());
            handleUnauthorized(response, e.getMessage());
        } catch (Exception e) {
            log.warn("##### Jwt토큰인증 도중 예외발생: {}; {}", e.getClass().getSimpleName(), e.getMessage());
            handleUnauthorized(response, e.getMessage());
        }
    }

    private Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .map(jwtService::removeBearerPrefix);
    }

    private Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .map(jwtService::removeBearerPrefix);
    }

    private String checkAccessTokenAndAuthentication(HttpServletRequest request) {
        String accessToken = extractAccessToken(request).orElseGet(this::loggingAndNull);
        String username = jwtService.extractAllClaims(accessToken).get(PARAM_USERNAME, String.class);
        if (username.isEmpty()) throw new JwtAuthExceptions.BedCredentialsException("accesstoken에 유저 식별 정보 useranme 없음");
        return username;
    }

    private String validateRefreshToken(String refreshToken) {
        Claims claims = jwtService.extractAllClaims(refreshToken);
        return refreshTokenService.getUsername(claims.get(PARAM_USERNAME, String.class));
    }

    private String reIssueRefreshToken(Map<String, Object> claims) {
        String reIssuedRefreshToken = jwtService.createBearerRefreshToken(claims);
        refreshTokenService.saveRefreshToken(reIssuedRefreshToken);
        return reIssuedRefreshToken;
    }

    private void reIssueRefreshAndAccessToken(HttpServletResponse response, String refreshToken) {
        String username = validateRefreshToken(refreshToken);
        sendAccessAndRefreshToken(response, jwtService.createBearerAccessToken(Map.of(PARAM_USERNAME, username)), reIssueRefreshToken(Map.of(PARAM_USERNAME, username)));
    }

    private void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, jwtService.withBearerPrefix(accessToken));
    }

    private void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, jwtService.withBearerPrefix(refreshToken));
    }

    private void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(accessHeader, jwtService.withBearerPrefix(accessToken));
    }

    private void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);
    }

    private void saveAuthentication(String username) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (userDetails != null) {
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private String loggingAndNull() {
        log.warn("##### AccessToken이 존재하지 않거나 유효하지 않은 접근");
        return "";
    }

    private void handleUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(Map.of("error", message)));
    }
}
