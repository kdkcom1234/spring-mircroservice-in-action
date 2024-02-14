# 프로젝트의 구성을 관리하는 공간

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