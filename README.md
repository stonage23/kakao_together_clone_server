# 카카오같이가치 백엔드 클론코딩

🔗https://together.kakao.com/

카카오 같이 가치 핵심 기능을 RESTful API으로 클론 코딩한 백엔드 서버 프로젝트입니다.

기간 : 2025. 07. 17 ~ 2025. 09. 24

<div> 
  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> 
  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> 
  <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
  <img src="https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white">
  <img src="https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens">
  <img src="https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white">
</div>
<br><br><br>

## 🔎 주요 기능
- [🏷️ 인증](#인증)
  - SpringSecurity 인증, 인가
  - JWT 토큰(accessToken, refreshToken) 기반 사용자 인증
- [🏷️ Redis 캐싱](#redis캐싱)
  - 회원가입, 비밀번호 수정 시 본인확인용 코드 캐싱
- [🏷️ 메일 발송](#메일발송)
  - SMTP 메일 기능 모듈화
- [🏷️ 결제](#결제)
  - 결제 위조 방지 및 사용자 경험 향상 지향
- [🏷️ 게시글](#게시글)
  - 임시저장 기능으로 사용자 경험 향상
- 🏷️ [파일업로드](#파일업로드)
  - 파일 스토리지 분리
  - 데이터 정합성을 위한 파일의 전체 생명주기 관리
- 🏷️ 애플리케이션 비즈니스
  - 유저
  - 기부
  - 모금
- [🏷️ 예외 처리](#예외처리)

<br><br><br>

---
## 🔎 ERD Structure
<img width="1397" height="754" alt="image" src="https://github.com/user-attachments/assets/cef6db98-c4d4-45e3-9e4f-d37e7dbd6d82" />

---
<br><br><br>
---
## 인증
### 📌 로그인 및 토큰 발급 흐름
🔗 관련 디렉토리
- 로그인 요청 처리[AuthService](src/main/java/com/kakao/together/service/auth/impl/AuthServiceImpl.java)
- 토큰 생성[JwtTokenProvider](src/main/java/com/kakao/together/token/JwtTokenProvider.java)
- 토근관련[token](src/main/java/com/kakao/together/token)
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
🔗 관련 파일
- accessToken 인증 필터 [JwtAuthenticationFilter](src/main/java/com/kakao/together/filter/JwtAuthenticationFilter.java)
- access토큰 파싱 및 인증객체 반환 클래스 [JwtTokenProvider](src/main/java/com/kakao/together/token/JwtTokenProvider.java)
- 토근관련[token](src/main/java/com/kakao/together/token)
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
🔗 관련 디렉토리
- 로그인 요청 처리[AuthService](src/main/java/com/kakao/together/service/auth/impl/AuthServiceImpl.java)
- 토큰 생성[JwtTokenProvider](src/main/java/com/kakao/together/token/JwtTokenProvider.java)
- 토큰관련[token](src/main/java/com/kakao/together/token)
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

## Redis캐싱
 🔗 [RedisService](src/main/java/com/kakao/together/external/redis/RedisService.java)
 - DB에 접속하지 않고 빠른 조회를 위해 Redis을 사용하여 캐시 기능을 구현했습니다.
 - Redis서버에 데이터를 저장하는 setData 함수는 일반 문자열 뿐 아니라 일반 객체도 저장할 수 있도록 메소드를 구분했습니다.
 - Redis 데이터 처리 중 발생하는 예외는 RedisServiceException.java라는 예외 클래스로 래핑하여 캐시 기능 호출부에서 적절한 예외처리를 할 수 있도록 구성하였습니다.
 - 실제로 프로젝트 내 redis 저장소를 사용하는 로직은 다음과 같습니다.
    - [PG사 토큰 캐싱](src/main/java/com/kakao/together/external/redis/RedisTokenProvider.java)
    - [refresh토큰 캐싱](src/main/java/com/kakao/together/external/redis/RedisTokenRepository.java)
<br><br><br>

## 메일발송
🔗 [SmtpMAilService](src/main/java/com/kakao/together/external/mail/SmtpMailService.java)<br>
🔗 [EmailTemplate](src/main/java/com/kakao/together/util/EmailTemplate.java)
- 메일 내용에 해당하는 부분은 별도의 Template 클래스로 분리하여 메일 발송 기능만 책임지도록 하였습니다.
- YAML 파일을 활용하여 애플리케이션의 설정 값과 비즈니스 상수들을 분리하고 모듈화함으로써 재사용성과 유지보수성을 향상시켰습니다.
<br><br><br>

## 결제
PaymentGate 결제 대행사로부터 결제 승인 정보(imp_uid, merchant_uid)을 받아 서버 내부 결제 교차검증을 진행하는 방식으로 결제 기능을 구현하였습니다. 
🔗 [결제검증서비스](src/main/java/com/kakao/together/service/paymentgate/impl/PortOneVerificationService.java)
```mermaid
flowchart TD
    A[Webhook 수신] --> B{PG API로 결제 조회};

    B --> C{API 호출 성공?};
    C -- Yes --> E{데이터 검증 성공?};
    C -- No --> D[400 Error 반환 / 재시도];

    E -- Yes --> H[내부 상태 '성공' 처리];
    H --> I[200 OK 반환 / 중단];
    
    E -- No --> F[내부 상태 '실패' 처리];
    F --> G[400 Error 반환 / 중단];
```

### 📌 중점을 둔 요소
- 단순 결제 검증 후 완료처리에서 그치지 않고 검증 성공/실패에 따라 서버 내부 데이터 상태의 생명주기를 관리할 수 있도록 하였습니다.
- PaymentGate에 매 요청 시 토큰을 재발급 받지 않도록 acceessToken을 redis으로 캐싱[RedisTokenProvider](src/main/java/com/kakao/together/external/redis/RedisTokenProvider.java)
  - 토큰이 존재하지 않는 경우에만 PG사에 토큰 발급 요청
  - 이식성을 고려하여 직렬화방식 점검하는 checkSerializerConfiguration 메소드 작성
- 유지보수 향상, 재사용을 위해 PG사 통신 로직, 서버 내 결제 정보 불러오는 로직, 결제 검증 로직, 결제 완료 후 처리 로직을 분리
  - PG사 통신 로직은 [PortOneClient](src/main/java/com/kakao/together/external/paymentgate/service/impl/PortOneClient.java)에서만 책임
  - 결제 검증 완료 후 처리 로직은 [PaymentInternalServiceImpl](src/main/java/com/kakao/together/service/payment/internal/PaymentInternalServiceImpl.java)에서 책임
  - 서버 내 결제 정보 불러오는 로직은 [PaymentDetailServiceImpl](src/main/java/com/kakao/together/service/paymentgate/impl/PaymentDetailsServiceImpl.java)에서 책임. 결제 검증 클래스에서 다른 클래스에 불필요하게 의존하는 것을 줄여주었습니다.
- 결제 검증 실패 시 트랜잭션 처리
  - 결제 검증 실패 시 서버 내부 결제 상태를 '실패'상태로 바꾸는 로직은 다른 트랜잭션의 영향에 받지 않도록 Propagation.REQUIRES_NEW 속성을 부여함.
    ```
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failCancelDonation(Long donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new NoSuchElementException("요청한 엔티티가 존재하지 않습니다; donationId: " + donationId));
        donation.failCancelDonation();
    }
    ```
- 다른 서비스와 마찬가지로 커스텀 예외를 정의해서 각 상황별로 대처할 수 있도록 하였습니다. 
  - [PaymentGateException](src/main/java/com/kakao/together/external/paymentgate/exception)(PG사 통신 예외)
    - PaymentGateResponseException, PaymentGateTokenException 등..
  - [PaymentException](src/main/java/com/kakao/together/exception/payment)(결제관련 예외)
    - PaymentVerificationException, PaymentCompleteException 등..

 <br><br><br>

 ## 파일업로드
파일 데이터의 정합성을 보장, 효율적인 관리를 위해 파일의 전체 생명주기를 관리하는 아키텍처를 설계했습니다.
- 클라이언트단 업로드 요청부터 파일의 최종 삭제까지 모든 단계를 추적 및 제어
- 고아 파일 발생을 원천적으로 차단하기 위한 노력(데이터 무결성 지향)

🖼️ 업로드 - 삭제까지의 파일 생명주기 흐름도
 ```mermaid

    graph LR
    A[사용자: 파일 업로드] --> B{임시 저장소<br/>FileInfo 상태: TEMP};

    subgraph "사용자 최종 결정"
        B -- 최종 저장 --> C[본문 저장 요청];
        B -- 저장 안함 / 이탈 --> D[방치된 임시 파일];
    end

    C --> E{실제 저장소<br/>FileInfo 상태: USED};

    subgraph "스케줄러 (자동 정리 프로세스)"
        D -.-> F[Batch: TEMP 상태 파일<br/>주기적으로 완전 삭제];
        H -.-> I[Batch: DELETED 상태 파일<br/>유예 기간 후 완전 삭제];
    end
    
    E -- 삭제 요청 --> G{FileInfo 상태: DELETED};
    G --> H[삭제 유예 기간 시작];

```
단계(파일상태)
- 임시업로드(TEMP)<br>
  사용자가 파일을 업로드하면 우선 임시 저장 디렉토리에 저장. 파일의 메타 데이터 생성
  - orphan 데이터 방지<br>
    사용자가 도중에 페이지를 이탈하거나 업로드를 정상적으로 완료하지 않아도 실제 저장소에 영향을 미치지 않습니다. 임시 저장소 내부 파일들은 스케줄러에 의해 주기적으로 정리됩니다.
- 영구저장(USED)<br>
  사용자가 실제 업로드 요청을 보내면 파일 메타데이터 상태를 USED으로 업데이트하고, 실제 파일을 임시 저장소 -> 실제 저장소로 이동시킵니다.
  - 정합성 보장<br>
    상태 변경과 파일 이동을 하나의 논리적 단위로 묶어 DB상태와 실제 파일의 위치 일치
- 삭제유예(DELETED)<br>
  해당 파일과 의존성을 가진 데이터가 삭제되는 경우 서버는 해당 파일을 바로 삭제하지 않고 DELETE 상태로 업데이트.
  - 시스템 안정성<br>
    복구가 필요하거나 의도치 않게 파일이 삭제된 경우 복원 가능성을 높여줍니다.
- 스케줄링을 통한 삭제<br>
  주기적으로 동작하는 스케줄러로 TEMP 상태의 파일, 삭제 유예기간이 지난 DELETED 상태의 파일을 물리적으로 영구 삭제합니다.
  - 자원 관리 자동화<br>
    서버 스스로 필요없는 파일을 정리
  - 시스템 부하 분산<br>
    시스템 부하가 큰 처리를 사용자 요청이 적은 시간대에 몰아서 수행하는 것을 가능하게 합니다. 

<br><br><br>

## 예외처리
### 📌 중점을 둔 요소
- 유지보수성 향상
  - 전역예외처리 클래스에서 일괄 예외 응답 처리를 담당하여 예외 응답 방식 수정은 이 파일에서만 책임지도록 하였습니다. 🔗[GlobalExceptionHandler](src/main/java/com/kakao/together/exception/GlobalExceptionHandler.java)
- 일관성 있는 응답
  - 발생가능 예외를 ErrorCode에 사전에 정의하였습니다. 🔗[ErrorCode](src/main/java/com/kakao/together/exception/ErrorCode.java)
  - 예외 유형에 따른 예외 응답 객체(ErrorResponse) 다르게 생성합니다. 🔗 [ErrorResponse](src/main/java/com/kakao/together/exception/ErrorResponse.java)
- 디버깅 용이성
  - 던져진 예외는 상단에서 CustomException으로 감싸서 전역 예외처리로 던져집니다. 🔗 [CustomException](src/main/java/com/kakao/together/exception/CustomException.java)
  - 저수준 모듈에서 발생한 예외들의 cause을 CustomException이 catch할 때까지 넘겨주어 예외 추적을 용이하게 만들었습니다. 


```mermaid
graph LR
    A["1. 저수준 계층: 기술 예외 발생<br/>(PaymentGateResponseException 등)"]
    --> B["2. 예외 전환 (Wrapping)<br/>도메인 예외로 변환<br/>(PaymentVerificationException 등)"]
    --> C["3. 서비스 계층: 예외 생성<br/>비즈니스 예외 생성<br/>(CustomException + ErrorCode)"]
    --> D{"4. 전역 핸들러: 중앙 처리<br/>(GlobalExceptionHandler)"}
    --> E["5. 최종 응답 생성<br/>일관된 JSON 응답 (ErrorResponse)"]
    --> F["클라이언트"]
```

응답 예시<br>
🖼️ 로그인이 필요한 경우
```
{
  "message": "로그인이 필요합니다. 로그인해주세요",
  "status": 401,
  "code": "REQUIRE_AUTHENTICATION",
  "errors": {}
}
```
🖼️ 유효성 검사 오류 시 
```
{
  "message": "적절하지 못한 요청입니다.",
  "status": 400,
  "code": "BAD_REQUEST",
  "errors": {
    "email": "이메일 형식이 올바르지 않습니다."
  }
}
```
🖼️ refresh 토큰이 만료된 경우
```
{
  "message": "만료된 토큰입니다. 재로그인해주세요.",
  "status": 401,
  "code": "EXPIRED_TOKEN",
  "errors": {}
}
```
































