# 스프링 마이크로 서비스 코딩 공작소(Spring Microservice in Action 2nd Edition)


## 1장 스프링, 클라우드와 만나다
- 마이크로 서비스 아키텍처 개념
- 마이크로 서비스 개발, 라우팅, 클라이언트 회복성, 보안 패턴
- 마이크로 서비스 로깅과 추적, 지표, 빌드/배포 패턴

---

## 2장 스프링 클라우드와 함께 마이크로서비스 세계 탐험

1. 스프링 클라우드 요소들에 대한 개념
  - 스프링 클라우드 컨피그, 서비스 디스커버리, 로드 밸런서, Resillence4j
  - 스프링 클라우드 게이트웨이, 스트림, 슬루스, 시큐리티
  - Resillence4j: 회로 차단기, 재시도, 벌크헤드 등 클라이언트 회복성 패턴 구현
  - 슬루스(Sleuth): 상관관계ID(correlation ID)로 트랜잭션 추적

2. 클라우드 네이티브 마이크로서비스 구축 방법
  - 12 팩터 앱 모범 사례 활용(https://12factor.net)
  - 코드베이스: 단일 코드베이스, 배포 환경(개발, 운영 등) 포함, 다른 마이크로 서비스와 공유하지 않음
  - 의존성: 의존성 관리도구를 이용하여 동일한 라이브러리 버전으로 마이크로서비스를 빌드함
  - 구성정보: 구성정보를 배포할 마이크로서비스와 분리해서 관리
  - 백엔드 서비스: 데이터베이스, 메시징 시스템과 같은 서드파티와 연결할 때 코드 변경없이 교체 가능해야함
  - 빌드, 릴리스, 실행: 코드가 빌드되면 변경 사항은 빌드 프로세스를 거쳐 재배포되어야 하고 빌드된 서비스는 변경 불가
  - 프로세스: 마이크로 서비스는 무상태이며, 상태 저장의 요구사항이 있다면 레디스 같은 캐시나 백업 데이터베이스를 사용
  - 포트 바인딩: 특정 포트로 서비스를 게시, 실행 파일로 패키징된 런타임. 명령줄을 통해 앱서버 없이 실행
  - 동시성: 프로세스를 많이 생성하여 서비스 부하를 분산, 스케일업과 스케일아웃
  - 폐기 가능: 다른 서비스에 영향을 주지 않고 새로운 인스턴스로 실행 및 실패한 인스턴스를 제거
  - 개발 및 운영 환경 일치: 각 환경의 배포 버전이 유사하여 개발환경과 운영 환경으로 신속히 이동할 수 있어야 함
  - 로그: 마이크로서비스는 표준출력(stdout)으로 로그를 기록하는데 집중하고, 로그를 관리하는 별도 도구로 관리한다. 
  - 관리 프로세스: 서비스 관리작업은 코드 저장소에 유지되는 스크립트로 수행해야 한다.

---

## 3장 스프링 부트로 마이크로서비스 구축하기
1. 프로젝트 기본 구성
  - 버전: 자바11, 스프링부트. 2.2.3
    - 본인은 자바17, 스프링부트 3.2.2 로 작업함
  - 빌드도구: Maven
  - 의존성: Spring Web, Actuator, Lombok

2. 프로젝트 작성
  - Controller(GET, POST, PUT, DELETE), POJO 클래스, Service 작성
  - mvn spring-boot:run
  - 국제화(i18n) 구현: LocaleResolver, ResourceBundleMessageSource를 사용
  - HATEOAS 구현: RepresentationModel을 사용
  - 액추에이터 설정: base-path, 활성 기능 설정

---

## 4장 도커
- 컨테이너와 도커는 마이크로서비스 환경의 이식성과 격리성을 도달하는데 주요한 도구로 사용된다.

### 1. Dockerfile 및 Docker CLI를 이용
#### Dockerfile
```
FROM eclipse-temurin:17-jre
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]
```
#### build & run
```shell
# jar
./mvnw package 
# image
docker build -t ostock/licensing-service:latest .
docker run -p 8080:8080 ostock/licensing-service:latest
```

### 2. Docker compose
#### docker-compose.yml
```yml
version: '3.7'
services:
  licensingservice:
    image: ostock/licensing-service:latest
    ports:
      - "8080:8080"
    environment:
      - "SPRING_PROFILES_ACTIVE=dev"
    networks:
      backend:
        aliases:
          - "licenseservice"

networks:
  backend:
    driver: bridge
```
#### compose run
```
docker compose up
docker compose down
```

### 3. Spring Boot에서 도커 빌드
- pom.xml
```xml
  <properties>
		<java.version>17</java.version>
		<kotlin.version>1.9.22</kotlin.version>
    <!-- 이미지 prefix -->
		<docker.image.prefix>ostock</docker.image.prefix>
	</properties>

  <!-- ... -->
  
  <plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <!-- 이미지명 설멍 -->
    <configuration>
      <image>
        <name>${docker.image.prefix}/${project.artifactId}:latest</name>
      </image>
    </configuration>
  </plugin>
```
```shell
./mvnw spring-boot:build-image
docker run -p8080:8080 ostock/licensing-service:latest
```

---

## 5장 스프링 클라우드 서버로 구성 관리
- 서비스와 구성을 분리한다
- 컨피그 서버 설정에서 현재 버전은 bootstrap.yml 대신에 application.yml을 사용
- 컨피그 클라이언트 설정에서 현재 버전은 아래와 같이 application.yml을 작성하여야 한다.

### application.yml(클라언트측)
```yml
spring:
  application:
    name: licensing-service
  config:
    import: optional:configserver:http://localhost:8071
```

### git 저장소 사용(컨피그서버측)
```yml
spring:
  profiles:
     active: git
  cloud:
    config:
      server:
          git:
            # 컨피그 서버/클라이언트 모두 기본 브랜치를 main으로 사용함
            uri: https://github.com/kdkcom1234/spring-mircroservice-in-action
            searchPaths: config
```

### Vault를 docker로 실행
```shell
# vault:버전을 명시적으로 작성해야 실행가능함
docker run -d -p 8200:8200 --name value -e 'VAULT_DEV_ROOT_TOKEN_ID=myroot' -e 'VAULT_DEV_LISTEN_ADDRESS=0.0.0.0:8200' vault:1.13.3
```

---

## 6장 서비스 디스커버리
- 서비스 소비자에게 서비스의 물리적 위치를 추상화, 새 서비스 인스턴스는 가용 서비스 풀에 추가되거나 제거될 수 있어 수평 확장/축소에 용이
- 서비스 인스턴스가 비정상정이면 디스커버리 엔진은 가용 서비스 목록에서 제거, 애플리케이션 회복성을 향상시킴

### 서비스 디스커버리 아키텍처
- 서비스 등록
- 클라이언트의 서비스 주소 검색
- 정보 공유
- 상태 모니터링

### 디스커버리 클라이언트 3가지 메커니즘
- 스프링 클라우드 Discovery Client: 수동으로 rest template 생성 및 수동으로 로드밸런싱 처리
- 스프링 클라우드 로드 밸런서를 지원하는 RestTemplate: 로드밸런스를 사용하는 rest template을 의존성 주입하여 호출
- 넷플릭스 Feign 클라이언트: interface로 API 스펙을 정의하여 요청 처리

---

## 7장 나쁜 상황에 대비한 스프링 클라우드와 Resililence4j를 사용한 회복성 패턴
- 클라이언트 측 회복성 소프트웨어 패턴들은 에러나 성능 저하로 원격 자원이 실패할 때 원격 자원의 클라이언트가 고장나지 않게 보호하는데 중점
- 클라이언트가 빨리 실패하고 데이터베이스 커넥션이나 스레드풀과 같은 자원을 소비하는 것을 방지한다.
- 성능이 낮은 원격 서비스 문제가 소비자에게 상향(upstream)으로 확산되는 것을 방지

### 클라이언트 측 회복성 패턴
- 클라이언트 측 로드 밸런싱: 레지스트리 캐시에서 문제를 탐지하면 서비스 풀에서 서비스 인스턴스를 제거하여 호출되지 않게 함
- 회로 차단기(circuit breaker): 원격 서비스가 호출될 때 호출을 모니터링, 호출이 너무 오래걸리면 차단기가 개입해서 호출을 종료
  - 빠른 실패(fail fast): 원격 서비스가 성능 저하를 겪으면 빠르게 실패하고 자원 고갈 이슈를 방지
  - 원만한 실패(fail gracefully): 사용자 의도를 충족하는 대체 매커니즘을 제공
  - 원활한 회복(recover seamlessly): 요청 중인 자원인 다시 온라인 상태가 되었는지 확인하고, 재접근을 허용하도록 주기적으로 확인 
- 폴백 처리: 원격 서비스 호출이 실패할 떄 예외(exception)를 생성하지 않고 서비스 소비자가 대체 코드 경로를 실행
- 벌크헤드(bulkhead): 원격 자원에 대한 호출을 자원별 스레드 풀로 분리, 특정 서비스가 느리게 응답하면 해당 풀만 포화됨

### Resilience4j 구현
- 회로 차단기: 요청받은 서비스가 실패할 때 요청을 중단
- 재시도(retry): 서비스가 일시적으로 실패할 때 재시도
- 벌크헤드(bulkhead): 과부하를 피하고자 동시 호출하는 서비스 요청 수를 제한
- 속도 제한(rate limit): 서비스가 한 번에 수신하는 호출 수를 제한한다.
- 폴백(fallback): 실패하는 요청에 대해 대체 경로를 설정
- 패턴 결합 순서: Retry(CircuitBreaker(RateLimiter(TimeLimiter(Bulkhead(Function)))))

### 회로 차단기 구현
- Resilience4j 회로 차단기 상태(State Machine)
  - 닫힌 상태(CLOSED): 
    - 링 비트 버퍼를 사용하여 요청의 상태를 저장
    - 링 비트 버퍼가 모두 채워졌고, 실패율이 임계치를 초과하면 열린 상태(OPEN)로 전이
  - 열린 상태(OPEN):
    - 설정된 시간 동안 호출은 모두 거부됨
    - 설정된 시간이 만료되면 반열린 상태(HALF-OPEN)로 전이
  - 반열린 상태(HALF-OPEN): 
    - 설정 가능한 다른 링 비트 버퍼를 사용하여 실패율을 평가
    - 실패율이 임계치보다 높으면 다시 열린 상태로 전이, 임계치보다 낮으면 닫힌 상태로 전이
- Resilience4j 회로 차단기의 링 비트 버퍼
  - 성공한 요청에는 0, 실패한 요청에는 1을 저장한다.
  - 링 비트 버퍼 크기가 모두 찼을 때만 임계치를 평가한다.
  - 예) 링 비트 버퍼 크기가 12이면 12번의 호출이 일어나야 임계치를 평가한다. 임계치가 50%이면 12개중에 실패가 6개
  - ** 슬라이딩 윈도 방식을 사용하고 있음, 기본은 count-based
    - 예) 슬라이딩 윈도 크기가 10이면 최근 10개 기준으로 임계치를 평가함 
    - https://resilience4j.readme.io/docs/circuitbreaker
    - https://jydlove.tistory.com/71
```yaml
resilience4j.circuitbreaker:
  instances:
    licenseService:
      # actuator에 상태를 등록함
      registerHealthIndicator: true
      # 최근 10개 단위로 성공률 측정
      slidingWindowSize: 10
      # 최소5개가 넘어가면 성공률 측정
      minimumNumberOfCalls: 5
      # HALF-OPEN 상태에서 CLOSED/OPEN 상태로 가기 위한 개수
      # 예) 임계치50%, 3회 호출에서 1회 실패면 CLOSED로 넘어감, 2회 실패면 OPEN으로 넘어감
      permittedNumberOfCallsInHalfOpenState: 3
      # 별도의 트리거 없이 OPEN에서 HALF-OPEN으로 넘어감
      automaticTransitionFromOpenToHalfOpenEnabled: true
      # OPEN 상태에서의 대기시간
      waitDurationInOpenState: 5s
      # 실패 임계치
      failureRateThreshold: 50
      # 서킷 브레이커 이벤트 버퍼 사이즈
      # /actuator/circuitbreakerevents 경로를 통해 확인 가능
      eventConsumerBufferSize: 10
      # 오류로 처리할 예외 클래스 목록
      recordExceptions:
        - org.springframework.web.client.HttpServerErrorException
        - java.util.concurrent.TimeoutException
        - java.io.IOException
```
### ThreadLocal과 Resilience4j
- Resilience4j에서 ThreadPool Bulkhead와 같이 함수를 별도의 스레드 풀에서 처리를 하는 경우
- 같은 부모 스레드의 특정 값을 자식 스레드인 벌크헤드 스레드에 전달하고자할 때 ThrealdLocal을 이용하여 전달할 수 있다.
- 부모 스레드에서 ThreadLocal Context에 set, 자식 스레드에서 get
- 예를 들어 서블릿 필터(부모스레드) -> 스레드풀벌크헤드 서비스(자식스레드)

---

## 8장 스프링 클라우드 게이트웨이를 이용한 서비스 라우팅

### service명으로 자동 라우팅
```yaml
management:
  endpoint:
    gateway:
      enabled: true

spring:
  cloud:
    gateway:
      discovery.locator:
        enabled: true
        lower-case-service-id: true
```
- http://게이트웨이서버:포트/actuator/routes 로 경로 맵핑 확인 가능
- http://게이트웨이서버:포트/서비스명/이하경로로 서비스 접근 가능
- 예) http://localhost:8072/licensing-service/v1/organization/d898a142-de44-466c-8c88-9ceb2c2429d3/license/f2a9c9d4-d2c0-44fa-97fe-724d77173c62/feign


### 수동 라우팅
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: organization-service # route의 id
        uri: lb://organization-service # lb://유레카에 등록된 서비스명

        predicates:
        # 경로(path)는 load() 메서드로 설정되지만, 여러 옵션 중 하나다.
        - Path=/organization/**

        filters: # 응답을 보내기 전이나 후에 요청 또는 응답을 수정하고자 스프링 web.filters들을 필터링한다.
        # 매개변수 및 교체 순서(replacement order)로 경로 정규식을 받아 요청 경로를 /organization/** 에서 /** 변경한다.
        - RewritePath=/organization/(?<path>.*), /$\{path}
```

### 동적으로 라우팅 구성을 재로딩
- Spring Boot Application에 @RefreshScope이 활성화 되어 있는 경우, 
- /actuator/refresh로 수동으로 추가된 경로 새로고침 가능
- 자동 라우팅의 경우에는 게이트웨이를 재시작해야만 적용된다.

---

## 9장 마이크로서비스 보안

### 키클록 보안의 네 가지 구성요소

- 보호 자원(protected resource)
  - 적절한 권한이 있는 인증된 사용자만 접근할 수 있는 자원(마이크로서비스)
- 자원 소유자(resource owner)
  - 사용자의 서비스 접근 권한 및 수행 가능 작업을 정의
- 애플리케이션(application)
  - 서비스를 호출하는 애플리케이션
- 인증 및 인가서버(authentication/authorization)
  - 애플리케이션과 서비스 사이의 중개자

### local 에서 실행
https://www.keycloak.org/getting-started/getting-started-zip
```shell
bin/kc.sh start-dev
```

### docker compose로 실행
```yaml
#version: '3.7'
services:
  keycloak:
    image: quay.io/keycloak/keycloak:24.0.1
    restart: always
    environment:
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin
    ports:
      - "8080:8080"
    command:
      - start-dev
```

### 사용자 설정
- Email Verified체크
- 이메일, First/Last name 모두 입력해야 토큰을 얻을 수 있다. 그렇지 않으면 토큰 조회시에 아래와 같은 메시지 출력
```json
{
    "error": "invalid_grant",
    "error_description": "Account is not fully set up"
}
```

### 액세스토큰 얻어오기
http://localhost:8080/realms/spmia-realm/protocol/openid-connect/token
Header
  Authorization: Basic client_id:client_secret
Body
  grant_type=password&username=사용자이름&password=비밀번호

```json
{
    "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIxbHVCT0tOTElicTIyREMzNmstLXpDT1luM3Z4MmVZY3dIbFdXbkhLQ3hnIn0.eyJleHAiOjE3MTA4Mjc3NDIsImlhdCI6MTcxMDgyNzQ0MiwianRpIjoiYmI3ZTQxZTMtNTFkNC00NWU3LWE3MzYtNDQ5OTI0ZDNlYzBmIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9zcG1pYS1yZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiJlNTA5YmQ5ZS00MjRmLTQ2NjgtYmNjMC0yOWMyZjU0M2ZhYTMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJvc3RvY2siLCJzZXNzaW9uX3N0YXRlIjoiNjZjOWYzYjctYzNjNC00YTg2LWI2ZDctZGUyNjI0ZmY3NmQyIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtc3BtaWEtcmVhbG0iLCJ1bWFfYXV0aG9yaXphdGlvbiIsIm9zdG9jay1hZG1pbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7Im9zdG9jayI6eyJyb2xlcyI6WyJBRE1JTiJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiNjZjOWYzYjctYzNjNC00YTg2LWI2ZDctZGUyNjI0ZmY3NmQyIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJpbGxhcnkgaHVheWx1cG8iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJpbGxhcnkuaHVheWx1cG8iLCJnaXZlbl9uYW1lIjoiaWxsYXJ5IiwiZmFtaWx5X25hbWUiOiJodWF5bHVwbyIsImVtYWlsIjoiaWxsYXJ5Lmh1YXlsdXBvQG9zdG9jay5jb20ifQ.PuT2q-VfN-Ssmyg1mulaBrw8YDwnT-EW1ogIO61TCTPee4GfBfI_S-BdvhO_y5DXuv191fBChPQ0eu_Kjc6fy9PEU4mHJUauJD6DTbs_2Kn7IfPShiR8QVoYEAR6jnticRwY4xWfervuqC3aO1DInGAKFDKB7QQy6UcqC3m-LV_lJA2hHVWi783blPN7RUKu1m-p6T8nVIeZQ1kyBffFwSI5Tig3y5k7RtxNO0uIlZp0-S_-VIpXV3y7ZdmISvIPXLvBiHGOdB7vXZzq1d-npWqF51L366Zcw7gu1pC2bZrfFyX4YJRcBH5f7jNp9x5Cd0QqsiBZHGx8h7C8d5JQ8A",
    "expires_in": 300,
    "refresh_expires_in": 1800,
    "refresh_token": "eyJhbGciOiJIUzUxMiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJjNzc2ZTdmMC03Yjg1LTRhZjEtOGZkZS03NGIxM2ZjMzgwYzEifQ.eyJleHAiOjE3MTA4MjkyNDIsImlhdCI6MTcxMDgyNzQ0MiwianRpIjoiMTkxNWUzOWEtMDIzMi00MGUwLWJmYzQtMmFiYTgxM2E2YzkyIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9zcG1pYS1yZWFsbSIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9yZWFsbXMvc3BtaWEtcmVhbG0iLCJzdWIiOiJlNTA5YmQ5ZS00MjRmLTQ2NjgtYmNjMC0yOWMyZjU0M2ZhYTMiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoib3N0b2NrIiwic2Vzc2lvbl9zdGF0ZSI6IjY2YzlmM2I3LWMzYzQtNGE4Ni1iNmQ3LWRlMjYyNGZmNzZkMiIsInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsInNpZCI6IjY2YzlmM2I3LWMzYzQtNGE4Ni1iNmQ3LWRlMjYyNGZmNzZkMiJ9.aJQ1d73KBaU8qgz_O-MXz_n1lJKREvTN3v2oZoKIJzMl0E3hckj4_NlKSQZxCSISAgA9S6G9eghtzwNE9ZLkIQ",
    "token_type": "Bearer",
    "not-before-policy": 0,
    "session_state": "66c9f3b7-c3c4-4a86-b6d7-de2624ff76d2",
    "scope": "profile email"
}
```


### 인증제어를 위한 서비스 환경구성
```xml
<!-- pom.xml -->
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
  </dependency>
</dependencies>
```
```yaml
# application.yml
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/spmia-realm
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8080/realms/spmia-realm/protocol/openid-connect/certs
```
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(registry -> registry
                        .anyRequest().authenticated()
                );

        return httpSecurity.build();
    }
}
```

### Authorization: Bearer 액세스토큰을 넣고 서비스 호출 
- GET http://localhost:8072/organization/v1/organization/d898a142-de44-466c-8c88-9ceb2c2429d3
- HEADER Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIxbHVCT0tOTElicTIyREMzNmstLXpDT1luM3Z4MmVZY3dIbFdXbkhLQ3hnIn0.eyJleHAiOjE3MTA4MjY1NTQsImlhdCI6MTcxMDgyNjI1NCwianRpIjoiNzM5YmRiZjEtZGZjYS00ZWI5LThiZDAtNjE3NmFlNmJkOWNkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9zcG1pYS1yZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiJlNTA5YmQ5ZS00MjRmLTQ2NjgtYmNjMC0yOWMyZjU0M2ZhYTMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJvc3RvY2siLCJzZXNzaW9uX3N0YXRlIjoiMTQ1YzUzZmEtNWE2Mi00MjgzLThmZWYtZTAzYzIyMjRiZTk3IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtc3BtaWEtcmVhbG0iLCJ1bWFfYXV0aG9yaXphdGlvbiIsIm9zdG9jay1hZG1pbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7Im9zdG9jayI6eyJyb2xlcyI6WyJBRE1JTiJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiMTQ1YzUzZmEtNWE2Mi00MjgzLThmZWYtZTAzYzIyMjRiZTk3IiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJpbGxhcnkgaHVheWx1cG8iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJpbGxhcnkuaHVheWx1cG8iLCJnaXZlbl9uYW1lIjoiaWxsYXJ5IiwiZmFtaWx5X25hbWUiOiJodWF5bHVwbyIsImVtYWlsIjoiaWxsYXJ5Lmh1YXlsdXBvQG9zdG9jay5jb20ifQ.DknocogrbggCNymww97U6E3aQjSCwhljLUQhy37NLPApYDGIXfel9Fq75uA97tP6EXZIkQUiuWJG0eYLkyWh8SaDZDPNKMl08h_lrvRU3_PUYqdSEGFn6Ah_RBTO5FHp7ShQedW04Y5tUuMaiJ-p6OSEndE_3apwRHMk9Y5-bgvUn04zpYG0mrF2bJyOdzYFkUlhRuDZaso9FM5fh1Mi0dT-dCUKAjvzDItCO8wcHZ1nRCWnvpm0WgxmakuYsGyuShXIO7oYLdRXZHFKT38Jbnr0ItlQ2mKKwrA3ciuXmpVBxB_f-tpNuqJKmE2zNbJ9bDavAPshc_rQL0ISIFUl7w


### Keycloak JWT의 resource_access role을 Spring Security의 Role로 맵핑
```json
{
  "resource_access": {
    "ostock": {
      "roles": [
        "ADMIN"
      ]
    }
  }
}
```
### 커스텀 컨버터 작성
```java
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = defaultGrantedAuthoritiesConverter.convert(jwt);
        authorities.addAll(extractCustomAuthorities(jwt));
        return new JwtAuthenticationToken(jwt, authorities);
    }

    private Collection<GrantedAuthority> extractCustomAuthorities(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess == null || !resourceAccess.containsKey("ostock")) {
            return List.of();
        }
        Map<String, Object> ostock = (Map<String, Object>) resourceAccess.get("ostock");
        List<String> roles = (List<String>) ostock.get("roles");
        return roles.stream()
                .map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
```

### Security Filter에 JWT컨버터 추가 및 @RollAllowed활성화
```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true) // @RolesAllowed 활성화
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2ResourceServer ->
                        oauth2ResourceServer.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(new CustomJwtAuthenticationConverter())
                        )
                );
        return http.build();
    }
}
```

### @RolesAllowed를 컨트롤러에서 사용
```java
@RestController
@RequestMapping(value="v1/organization")
public class OrganizationController {
    @Autowired
    private OrganizationService service;

    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @RequestMapping(value = "/{organizationId}", method = RequestMethod.GET)
    public ResponseEntity<Organization> getOrganization(@PathVariable("organizationId") String organizationId, HttpServletRequest req) {
        System.out.println(req.getHeader("Authorization"));
        return ResponseEntity.ok(service.findById(organizationId));
    }
}
```


### FeignClient를 다른 서비스 호출시에 헤더값 추가
```java
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // interceptor logic
        template.header(UserContext.CORRELATION_ID, UserContextHolder.getContext().getCorrelationId());
        template.header(UserContext.AUTH_TOKEN, UserContextHolder.getContext().getAuthToken());
        template.header(UserContext.AUTHORIZATION, UserContextHolder.getContext().getAuthorization());
    }
}
```
```java
@FeignClient(value = "organization-service", configuration = {FeignClientInterceptor.class})
public interface OrganizationFeignClient {
    @CircuitBreaker(name = "organizationService")
    @GetMapping(value = "/v1/organization/{organizationId}", consumes = "application/json")
    Organization getOrganization(@PathVariable String organizationId);
}

```
