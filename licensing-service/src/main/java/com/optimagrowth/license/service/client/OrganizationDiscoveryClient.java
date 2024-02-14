package com.optimagrowth.license.service.client;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import com.optimagrowth.license.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

// 수동으로 rest template 생성 및 수동으로 로드밸런싱 처리
@Component
public class OrganizationDiscoveryClient {

    @Autowired
    private DiscoveryClient discoveryClient;

    public Organization getOrganization(String organizationId) {
        RestTemplate restTemplate = new RestTemplate();
        // 조직 서비스의 모든 인스턴스 리스트를 얻는다.
        List<ServiceInstance> instances = discoveryClient.getInstances("organization-service");

        if(instances.size() == 0) return null;
        // 첫번쨰 인스턴스에서 uri를 가져온다.
        String serviceUri = String.format("%s/v1/organization/{organizationId}",
                instances.get(0).getUri().toString());
        System.out.println(serviceUri);
        // rest 호출
        ResponseEntity<Organization> restExchange =
                restTemplate.exchange(
                        serviceUri, HttpMethod.GET,
                        null, Organization.class, organizationId);

        return restExchange.getBody();
    }
}
