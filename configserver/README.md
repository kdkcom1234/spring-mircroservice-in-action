# 프로젝트의 구성 서버

## 패키징 및 이미지 빌드
```shell
# jar
./mvnw package -DskipTests
# image
docker build -t ostock/config-server:latest .
```

## 아래와 같이 구성을 각 서비스에 조회할 수 있다.
```
GET http://구성서버:포트/서비스명(spring.application.name)/환경

예)
GET http://localhost:8071/licensing-service/dev
GET http://localhost:8071/licensing-service/default
```

