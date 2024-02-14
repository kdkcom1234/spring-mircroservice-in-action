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
