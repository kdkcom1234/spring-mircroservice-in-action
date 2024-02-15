package com.optimagrowth.license.service.client;

import com.optimagrowth.license.model.Organization;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("organization-service")
public interface OrganizationFeignClient {
    @CircuitBreaker(name = "organizationService")
    @GetMapping(value = "/v1/organization/{organizationId}", consumes = "application/json")
    Organization getOrganization(@PathVariable String organizationId);
}
