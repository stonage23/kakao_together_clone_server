# 카카오같이가치 백엔드 클론코딩

🔗https://together.kakao.com/

카카오 같이 가치 핵심 기능을 RESTful API으로 클론 코딩한 백엔드 서버 프로젝트입니다.
<br><br><br>

---
## ERD Structure
<img width="1397" height="754" alt="image" src="https://github.com/user-attachments/assets/cef6db98-c4d4-45e3-9e4f-d37e7dbd6d82" />

---
<br><br><br>

## 🔎 주요 기능
### 보안
- SpringSecurity 인증, 인가
- JWT 토큰(accessToken, refreshToken) 기반 사용자 인증
### Redis 캐싱
- 회원가입, 비밀번호 수정 시 본인확인용 코드 캐싱
### 메일 발송
- SMTP 메일 기능 모듈화
### 결제
- 결제 위조 방지 및 사용자 경험 향상 지향
### 게시글
- 임시저장 기능으로 사용자 경험 향상
### 파일 업로드
- 파일 스토리지 분리
- 데이터 정합성을 위한 파일의 전체 생명주기 관리
### 애플리케이션 비즈니스
- 유저 
- 기부
- 모금
### 예외 처리

<br><br><br>
---
## 🏷️보안

### 📌 로그인 및 토큰 발급 흐름
```mermaid
sequenceDiagram
    participant Client
    participant AuthService
    participant AuthenticationManager
    participant TokenProvider
    participant JwtService

    Client->>AuthService: 1. 로그인 요청 (ID, PW)
    activate AuthService
    AuthService->>AuthenticationManager: 2. 인증 위임 (UsernamePasswordAuthenticationToken)
    activate AuthenticationManager
    AuthenticationManager-->>AuthService: 3. 인증 성공, Authentication 객체 반환
    deactivate AuthenticationManager
    AuthService->>TokenProvider: 4. 토큰 생성 요청 (Authentication)
    activate TokenProvider
    TokenProvider->>JwtService: 5. Access/Refresh 토큰 생성 위임
    activate JwtService
    JwtService-->>TokenProvider: 6. 생성된 토큰 반환
    deactivate JwtService
    TokenProvider-->>AuthService: 7. 토큰 컨테이너(DTO) 반환
    deactivate TokenProvider
    AuthService-->>Client: 8. Access/Refresh 토큰 응답
    deactivate AuthService
```

### 📌 access token 기반 인증 흐름
```mermaid
sequenceDiagram
    participant Client
    participant JwtAuthenticationFilter
    participant TokenProvider
    participant SecurityContextHolder
    participant Controller

    Client->>JwtAuthenticationFilter: 1. API 요청 (Header: Bearer AccessToken)
    activate JwtAuthenticationFilter
    JwtAuthenticationFilter->>TokenProvider: 2. 토큰 검증 및 Authentication 객체 생성 요청
    activate TokenProvider
    TokenProvider-->>JwtAuthenticationFilter: 3. 토큰에서 추출한 정보로 Authentication 객체 반환
    deactivate TokenProvider
    JwtAuthenticationFilter->>SecurityContextHolder: 4. SecurityContext에 Authentication 객체 저장
    JwtAuthenticationFilter->>Controller: 5. 다음 필터/컨트롤러로 요청 전달
    deactivate JwtAuthenticationFilter
    Controller-->>Client: 6. API 응답
```

### 📌 토큰 재발급 흐름
```mermaid
sequenceDiagram
    participant Client
    participant AuthService
    participant TokenProvider
    participant JwtService

    Client->>AuthService: 1. 토큰 재발급 요청 (RequestBody: RefreshToken)
    activate AuthService
    AuthService->>TokenProvider: 2. Refresh Token 검증 및 Authentication 객체 생성 요청
    activate TokenProvider
    TokenProvider-->>AuthService: 3. 토큰에서 추출한 정보로 Authentication 객체 반환
    deactivate TokenProvider
    AuthService->>TokenProvider: 4. 새로운 토큰 생성 요청 (Authentication)
    activate TokenProvider
    TokenProvider->>JwtService: 5. 새로운 Access/Refresh 토큰 생성 위임
    activate JwtService
    JwtService-->>TokenProvider: 6. 생성된 토큰 반환
    deactivate JwtService
    TokenProvider-->>AuthService: 7. 새로운 토큰 컨테이너(DTO) 반환
    deactivate TokenProvider
    AuthService-->>Client: 8. 새로운 Access/Refresh 토큰 응답
    deactivate AuthService
```
<br><br><br>

## 예외처리
### 📌 중점을 둔 요소
- 일관성 있는 응답
- 유지보수성 향상
- 디버깅 시 예외 추적

```mermaid
graph LR
    A["1. 저수준 계층: 기술 예외 발생<br/>(PaymentGateResponseException 등)"]
    --> B["2. 예외 전환 (Wrapping)<br/>도메인 예외로 변환<br/>(PaymentVerificationException 등)"]
    --> C["3. 서비스 계층: 예외 생성<br/>비즈니스 예외 생성<br/>(CustomException + ErrorCode)"]
    --> D{"4. 전역 핸들러: 중앙 처리<br/>(GlobalExceptionHandler)"}
    --> E["5. 최종 응답 생성<br/>일관된 JSON 응답 (ErrorResponse)"]
    --> F["클라이언트"]
```

