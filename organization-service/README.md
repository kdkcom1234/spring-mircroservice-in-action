# 라이센스 서비스

## 패키징 및 이미지 빌드
```shell
# jar
./mvnw package -DskipTests
# image
docker build -t ostock/organization-service:latest .
```