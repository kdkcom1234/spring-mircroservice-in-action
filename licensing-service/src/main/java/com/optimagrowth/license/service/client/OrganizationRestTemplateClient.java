package com.optimagrowth.license.service.client;

import com.optimagrowth.license.model.Organization;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

// 로드밸런스를 사용하는 rest template을 의존성 주입하여 호출
@Component
public class OrganizationRestTemplateClient {
    @Autowired
    RestTemplate restTemplate;

    @CircuitBreaker(name = "organizationService")
    public Organization getOrganization(String organizationId) {
        ResponseEntity<Organization> restExchange = restTemplate.exchange(
                "http://organization-service/v1/organization/{organizationId}",
                HttpMethod.GET, null,
                Organization.class, organizationId);
        return restExchange.getBody();
    }
}
