package com.optimagrowth.license;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
// 스프링 구성정보를 다시읽게하는 /actuator/refresh 엔드포인트 활성화
// 컨피그 서버에서 설정이 바귀면 스프링 클라우드 버스를 통해 게시하는 방법 존재
// 컨피그를 수정하면 디스커버리 서버에 질의하여 서비스의 모든 인스턴스를 찾고 /refresh를 호출하는 스크립트를 작성하는 방법도 가능
@RefreshScope
// eureka discovery client 사용
@EnableDiscoveryClient
public class LicenseServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LicenseServiceApplication.class, args);
	}

}
