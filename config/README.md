# 프로젝트의 구성을 관리하는 공간

## 구성 파일명
```shell
[spring.applicaion.name]-[spring.profiles].[yml/properties]
# 예)
# 기본 및 로컬
licensing-service.properties
# 개발환경
licensing-service-dev.properties
```

## config server의 설정
```yaml
spring:
  application:
    name: config-server
  profiles:
     active: git
  cloud:
    config:
      server:
          git:
            uri: https://github.com/kdkcom1234/spring-mircroservice-in-action
            searchPaths: config
```