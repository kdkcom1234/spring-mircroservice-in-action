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
