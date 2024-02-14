# 프로젝트를 Docker로 실행하는 공간

## docker-compose로 실행
```shell
# 전체시작 
docker compose up
# 전체 종료
docker compose down
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
        ## docker ps, STATUS에 상태 표시 Up 40 seconds (healthy)
        test: ["CMD-SHELL", "curl -f http://configserver:8071/actuator/health | grep UP || exit 1"]
        start_period: 5s
        start_interval: 5s
        interval: 5s
        timeout: 3s
        retries: 10
```
### health check 설명 by chatgpt
```yml
test: ["CMD-SHELL", "curl -f http://configserver:8071/actuator/health | grep UP || exit 1"]
```

Docker Compose의 `healthcheck` 설정은 컨테이너가 정상적으로 동작하고 있는지 확인하기 위해 사용됩니다. 여기서 제공된 설정은 특정 컨테이너의 "건강 상태"를 평가하기 위한 명령어를 정의하고 있습니다. 구체적으로 설명하자면:

1. **`test`:** 이 항목은 건강 체크를 수행할 때 실행할 테스트 명령어를 정의합니다.
   - **`["CMD-SHELL", "<command>"]`:** 이 형식은 셸 명령어를 실행하도록 지시합니다. 여기서 `<command>` 부분에 실제 실행할 명령어를 넣습니다.
2. **실제 명령어:**
   - **`curl -f http://configserver:8071/actuator/health`:** 이 명령어는 `curl`을 사용하여 `http://configserver:8071/actuator/health` 주소에 HTTP 요청을 보냅니다. `-f` 플래그는, 만약 HTTP 요청이 실패 상태 코드(예: 400이상)를 반환하면 `curl`이 오류와 함께 종료되도록 합니다.
   - **`| grep UP`:** `curl` 명령어의 출력을 `grep` 명령어로 전달합니다. `grep UP`은 출력 중에서 "UP"이라는 문자열을 찾습니다. 이는 많은 건강 체크 엔드포인트에서 서비스가 정상적으로 동작하고 있음을 나타내는 데 사용되는 표준 메시지입니다.
   - **`|| exit 1`:** 이 부분은 이전 명령어(`grep`)가 실패할 경우(즉, "UP" 문자열을 찾지 못할 경우) `exit 1`을 실행하여 비정상적인 상태(오류 코드 1)로 종료하도록 합니다. Docker는 이를 컨테이너의 건강이 좋지 않음으로 해석합니다.