# 프로젝트를 Docker로 실행하는 공간

## docker-compose로 실행
```shell
# 전체시작 
docker-compose up
# 전체 종료
docker-compose down
# 특정 서비스 종료
docker compose stop licensingservice
docker compose down licensingservice
# 특정 서비스 시작
docker compose up licensingservice
# 특정 서비스 로그 확인
docker compose logs licensingservice
docker compose logs -f licensingservice
```

## 서비스 설정
```yaml
  licensingservice:
    image: ostock/licensing-service:latest
    container_name: licensingservice
    hostname: licensingservice
    # 개발환경 프로필
    # 개발환경의 컨피그 서버 경로는 컴포즈 파일에 설정
    environment:
      - "SPRING_PROFILES_ACTIVE=dev"
      - "SPRING_CONFIG_IMPORT=optional:configserver:http://configserver:8071"
    # 다른 백엔드 인프라가 정상 상태이면 기동한다.
    depends_on:
      database:
        condition: service_healthy
      configserver:
        condition: service_healthy
      eurekaserver:
        condition: service_healthy
    ports:
      - "8080:8080"
    # bridge 네트워크를 사용
    networks:
      - backend
```

### 벡엔드 인프라 설정
```yaml
  configserver:
    image: ostock/config-server:latest
    container_name: configserver
    hostname: configserver
    ports:
      - "8071:8071"
    networks:
      - backend
    healthcheck:
        ## actuator를 통해 헬스체크를 한다
        ## STATUS에 상태 표시 Up 40 seconds (healthy)
        test: ["CMD-SHELL", "curl -f http://configserver:8071/actuator/health || exit 1"]
        start_period: 5s
        start_interval: 5s
        interval: 5s
        timeout: 3s
        retries: 10
```