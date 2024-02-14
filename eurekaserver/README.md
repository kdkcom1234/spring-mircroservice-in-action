# 프로젝트의 서비스 레지스트리

## 패키징 및 이미지 빌드
```shell
# jar
./mvnw package -DskipTests
# image
docker build -t ostock/eureka-server:latest .
```